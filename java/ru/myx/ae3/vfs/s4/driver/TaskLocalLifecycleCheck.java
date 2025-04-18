package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.Engine;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.util.BasicQueue;
import ru.myx.util.CounterFifoLimited;
import ru.myx.util.FifoQueueLinked;

final class TaskLocalLifecycleCheck //
		extends
			TaskCommon<Void> {

	static enum TaskState { //
		/** CHECK_FRESH */
		CK_FRESH, //
		/** CHECK_REFERENCED */
		CK_REFER, //
		/** LOOP_FINISHED */
		LOOP, //
		/** READ_FRESH */
		RD_FRESH, //
		/** READ_REFERENCED */
		RD_REFER, //
		/**  */
		READY, //
		;
	}

	final BasicQueue<RecImpl> checkedReferenced;

	final BasicQueue<RecImpl> checkedUnreachable;

	long executed;

	final BasicQueue<RecImpl> pending;

	private final CounterFifoLimited referencedTailStats;

	double rewriteProbability;

	final BasicQueue<RecImpl> scheduled;

	short scheduleValue;

	TaskState state;

	int stFound, stChangedSlot, stUnreferenced, stDropped;

	long stStarted;

	private final TaskLocalBatchProcessReferenced taskBatchProcessCheckedFresh;

	private final TaskLocalBatchProcessReferenced taskBatchProcessCheckedReferenced;

	private final TaskLocalLifecycleFixQueue taskStorageFixQueue;

	TaskLocalLifecycleCheck() {

		this.state = TaskState.READY;
		// skip first run
		this.executed = Engine.fastTime();
		this.scheduled = new FifoQueueLinked<>();
		this.pending = new FifoQueueLinked<>();
		this.checkedReferenced = new FifoQueueLinked<>();
		this.checkedUnreachable = new FifoQueueLinked<>();
		this.taskStorageFixQueue = new TaskLocalLifecycleFixQueue();
		this.taskBatchProcessCheckedFresh = new TaskLocalBatchProcessReferenced(
				TaskLocalBatchProcessReferenced.TaskState.EXEC_FRESH, //
				64,
				this.checkedReferenced,
				this.checkedUnreachable,
				this);
		this.taskBatchProcessCheckedReferenced = new TaskLocalBatchProcessReferenced(
				TaskLocalBatchProcessReferenced.TaskState.EXEC_REFERENCED, //
				64,
				this.checkedReferenced,
				this.checkedUnreachable,
				this);
		this.referencedTailStats = new CounterFifoLimited(4, 32);
	}

	/** @param realTicks */
	private void dumpLoop(final S4DriverAbstract local, final int realTicks) {

		final int totalChangedSlot = this.taskBatchProcessCheckedFresh.stChangedSlot + this.taskBatchProcessCheckedReferenced.stChangedSlot;
		final int totalUnreferenced = this.taskBatchProcessCheckedFresh.stUnreferenced + this.taskBatchProcessCheckedReferenced.stUnreferenced;
		final int totalDropped = this.taskBatchProcessCheckedFresh.stDropped + this.taskBatchProcessCheckedReferenced.stDropped;

		final int nowChangedSlot = totalChangedSlot - this.stChangedSlot;
		final int nowUnreferenced = totalUnreferenced - this.stUnreferenced;
		final int nowDropped = totalDropped - this.stDropped;

		this.stChangedSlot = totalChangedSlot;
		this.stUnreferenced = totalUnreferenced;
		this.stDropped = totalDropped;

		assert StorageImplS4.log(//
				EventLevel.INFO, //
				"LIFECYCLE-CHECK",
				"DONE",
				local.getKey() //
						+ ", " + this.state //
						+ ", ticks=" + realTicks //
						+ ", slot=" + realTicks % 3 //
						+ ", schedule=" + this.scheduleValue//
						+ (this.state == TaskState.CK_FRESH || this.state == TaskState.RD_FRESH
							? this.state == TaskState.RD_FRESH
								? ", pending: " + this.stFound //
								: ", checked/updated/deleted: " + this.stFound + "/" + nowChangedSlot + "/" + nowDropped //
							: this.state == TaskState.CK_REFER || this.state == TaskState.RD_REFER
								? (this.state == TaskState.RD_REFER
									? ", pending: " + this.stFound //
											+ ", stats=" + this.referencedTailStats.getCount() //
									: ", checked/updated/unused: " + this.stFound + "/" + nowChangedSlot + "/" + nowUnreferenced//
											+ ", stats=" + this.referencedTailStats.getCount() //
											+ ", rewrite=" + Format.Compact.toDecimal(this.rewriteProbability * 100.0) + "%" //
								) //
										+ ", avgTask=" + Format.Compact.toDecimal(this.referencedTailStats.getAverage())//
								: "") //
						+ ", took=" + Format.Compact.toPeriod(System.currentTimeMillis() - this.stStarted) //
		);
	}

	@Override
	public String toString() {

		return "TASK_LIFECYCLE_CHECK{state:" + this.state + "}";
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		final int realTicks = context.local.scheduleTicks & 0x7FFFFFFF;
		final int scheduleTicks = realTicks / 6;
		switch (this.state) {
			case READY : {
				/** Task called with 10 seconds intervals, our schedule is per-minute */
				if (realTicks % 7 == 3) {
					final long current = Engine.fastTime();
					if (this.taskStorageFixQueue.state == TaskLocalLifecycleFixQueue.TaskState.READY && this.taskStorageFixQueue.executed + 10_000L < current) {
						this.taskStorageFixQueue.executed = current;
						synchronized (this.taskStorageFixQueue) {
							context.enqueue(this.taskStorageFixQueue);
						}
					}
				}
				this.stStarted = System.currentTimeMillis();
				this.state = realTicks % 2 == 0
					? TaskState.RD_FRESH
					: TaskState.RD_REFER//
				;
				this.scheduleValue = realTicks % 2 == 0
					? S4ScheduleType.getScheduledFresh(realTicks, scheduleTicks)
					: S4ScheduleType.getScheduledReferenced(realTicks, scheduleTicks)//
				;

				context.worker.searchScheduled(this.scheduleValue, Integer.MAX_VALUE, this.scheduled);
				context.enqueue(this);
				return;
			}
			case RD_REFER :
			case RD_FRESH : {
				final int found = TaskLocalBatchCheckReferenced.preprocessCheckCached(//
						context,
						this.scheduled,
						this.pending,
						this.checkedReferenced//
				);
				this.stFound = found;
				if (Report.MODE_ASSERT) {
					this.dumpLoop(context.local, realTicks);
				}
				if (found > 0) {
					if (TaskState.RD_FRESH == this.state) {
						this.state = TaskState.CK_FRESH;
						this.rewriteProbability = 1.0;
					} else {
						this.state = TaskState.CK_REFER;
						final double average = this.referencedTailStats.getAverage();
						this.rewriteProbability = Double.isNaN(average)
							? 1.0 / 64.0
							: found / (1.0 + average) / 32.0;
						this.referencedTailStats.register(found);
					}
					/** extra enqueue 8-(, need to process referenced even if there is nothing to
					 * check */
					context.enqueue(this);
					return;
				}
				if (this.state != TaskState.RD_FRESH) {
					this.referencedTailStats.register(0);
				}

				this.state = TaskState.LOOP;
			}
			//$FALL-THROUGH$
			case LOOP : {
				{
					S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, ?> xct = context.createNewWorkerTransaction();
					try {
						// update ticks
						xct.arsLinkUpsert( //
								context.local.rootRecord,
								S4Driver.KEY_SCHEDULE_TICKS,
								TreeLinkType.LOCAL_PRIVATE_REFERENCE,
								Engine.fastTime(),
								Guid.forJavaInteger(++context.local.scheduleTicks)//
						);
						// commit transaction
						xct.commit();
						xct = null;
					} finally {
						if (xct != null) {
							xct.cancel();
							xct = null;
						}
					}
					// commit ongoing changes
					context.worker.commitLowLevel();
				}
				this.setResult(null);
				this.state = TaskState.READY;
				this.executed = Engine.fastTime();
				return;
			}
			case CK_REFER :
			case CK_FRESH : {
				if (this.checkedReferenced.hasNext() || this.checkedUnreachable.hasNext()) {
					if (this.state == TaskState.CK_FRESH) {
						context.enqueue(this.taskBatchProcessCheckedFresh);
					} else {
						this.taskBatchProcessCheckedReferenced.rewriteProbability = this.rewriteProbability;
						context.enqueue(this.taskBatchProcessCheckedReferenced);
					}
					return;
				}
				if (this.pending.hasNext()) {
					context.enqueue(//
							new TaskLocalBatchCheckReferenced(//
									this.pending,
									128,
									this.checkedReferenced,
									this.rewriteProbability,
									this.checkedUnreachable,
									this//
							)//
					);
					return;
				}
				{
					if (Report.MODE_ASSERT) {
						this.dumpLoop(context.local, realTicks);
					}
					this.state = TaskState.LOOP;
					context.enqueue(this);
					return;
				}
			}
			default :
		}
	}
}
