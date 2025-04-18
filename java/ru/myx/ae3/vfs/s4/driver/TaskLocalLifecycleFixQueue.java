package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.Engine;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.util.BasicQueue;
import ru.myx.util.FifoQueueLinked;

final class TaskLocalLifecycleFixQueue //
		extends
			TaskCommon<Void> {

	static enum TaskState {
		CHECK, LOOP, READ_LOOP, READ_RANDOM, READY,;
	}

	final BasicQueue<RecImpl> checkedReferenced;

	final BasicQueue<RecImpl> checkedUnreachable;

	long executed;

	final BasicQueue<RecImpl> invalids;

	final BasicQueue<RecImpl> pending;

	TaskState state;

	private final TaskLocalBatchProcessReferenced taskBatchProcessChecked;

	TaskLocalLifecycleFixQueue() {

		this.state = TaskState.READY;
		// skip first run
		this.executed = Engine.fastTime();
		this.invalids = new FifoQueueLinked<>();
		this.pending = new FifoQueueLinked<>();
		this.checkedReferenced = new FifoQueueLinked<>();
		this.checkedUnreachable = new FifoQueueLinked<>();
		this.taskBatchProcessChecked = new TaskLocalBatchProcessReferenced(
				TaskLocalBatchProcessReferenced.TaskState.EXEC_INVALID, //
				64,
				this.checkedReferenced,
				this.checkedUnreachable,
				this);
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		final int realTicks = context.local.scheduleTicks & 0x7FFFFFFF;
		final int scheduleTicks = realTicks / 6;
		switch (this.state) {
			case READY : {
				final short reservedOffset = S4ScheduleType.T06_RESERVED.baseOffset();
				final short reservedTicks = S4ScheduleType.T06_RESERVED.scheduleTicks();

				final short dS1 = (short) (reservedOffset + scheduleTicks % reservedTicks);
				final short dS2 = (short) (-1 - scheduleTicks % Short.MAX_VALUE);

				this.state = TaskState.READ_LOOP;

				this.setResult(null);
				context.worker.searchScheduled(dS1, Integer.MAX_VALUE, this.invalids);
				context.worker.searchScheduled(dS2, Integer.MAX_VALUE, this.invalids);
				context.enqueue(this);
				return;
			}
			case READ_LOOP : {
				this.state = TaskState.READ_RANDOM;

				if (!this.invalids.hasNext()) {
					final short reservedOffset = S4ScheduleType.T06_RESERVED.baseOffset();
					final short reservedTicks = S4ScheduleType.T06_RESERVED.scheduleTicks();

					final short dR1 = (short) (reservedOffset + Engine.createRandom(reservedTicks));
					final short dR2 = (short) (-1 - Engine.createRandom(Short.MAX_VALUE));

					context.worker.searchScheduled(dR1, Integer.MAX_VALUE, this.invalids);
					context.worker.searchScheduled(dR2, Integer.MAX_VALUE, this.invalids);
					context.enqueue(this);
					return;
				}
			}
			//$FALL-THROUGH$
			case READ_RANDOM : {
				TaskLocalBatchCheckReferenced.preprocessCheckCached(//
						context,
						this.invalids,
						this.pending,
						this.checkedReferenced//
				);
				if (this.pending.hasNext()) {
					StorageImplS4.log(EventLevel.WARNING, "SCHEDULE(" + context.local.scheduleTicks + ")", "LIFECYCLE-FIXQUEUE", "found schedule records to fix! s4d=" + context);
					this.state = TaskState.CHECK;
					context.enqueue(this);
					return;
				}
				if (this.checkedReferenced.hasNext()) {
					context.enqueue(this.taskBatchProcessChecked);
					return;
				}

				this.state = TaskState.LOOP;
			}
			//$FALL-THROUGH$
			case LOOP : {
				final int nowChangedSlot = this.taskBatchProcessChecked.stChangedSlot;
				final int nowUnreferenced = this.taskBatchProcessChecked.stUnreferenced;
				final int nowDropped = this.taskBatchProcessChecked.stDropped;
				if (nowChangedSlot != 0 || nowUnreferenced != 0 || nowDropped != 0) {
					this.taskBatchProcessChecked.stChangedSlot = this.taskBatchProcessChecked.stUnreferenced = this.taskBatchProcessChecked.stDropped = 0;
					StorageImplS4.log(
							EventLevel.NOTICE,
							"SCHEDULE(" + context.local.scheduleTicks + ")",
							"LIFECYCLE-FIXQUEUE",
							"finished fixing schedule records for this iteration! " + (nowChangedSlot + nowUnreferenced + nowDropped) + " records checked, " + nowChangedSlot
									+ " records re-scheduled, " + nowUnreferenced + " records un-referenced, " + nowDropped + " records dropped" + ", ticks=" + realTicks + ", s4d="
									+ context);
				}
				StorageImplS4.log(EventLevel.DEBUG, "SCHEDULE(" + context.local.scheduleTicks + ")", "LIFECYCLE-FIXQUEUE", "all done");
				this.setResult(null);
				this.state = TaskState.READY;
				this.executed = Engine.fastTime();
				return;
			}
			case CHECK :
				if (this.checkedReferenced.hasNext() || this.checkedUnreachable.hasNext()) {
					context.enqueue(this.taskBatchProcessChecked);
					return;
				}
				if (this.pending.hasNext()) {
					context.local.enqueue(//
							new TaskLocalBatchCheckReferenced(//
									this.pending,
									128,
									this.checkedReferenced,
									1.0,
									this.checkedUnreachable,
									this)//
					);
					return;
				} {
				this.state = TaskState.LOOP;
				context.enqueue(this);
				return;
			}
			default :
		}
	}

	@Override
	public String toString() {

		return "TASK_LIFECYCLE_FIX_QUEUE{state:" + this.state + "}";
	}
}
