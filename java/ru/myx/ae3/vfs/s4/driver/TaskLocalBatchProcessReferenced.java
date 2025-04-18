package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.util.BasicQueue;

final class TaskLocalBatchProcessReferenced //
		extends
			TaskCommon<Void> {

	static enum TaskState {
		EXEC_FRESH, EXEC_INVALID, EXEC_REFERENCED;
	}

	final TaskCommon<?> callback;

	final BasicQueue<RecImpl> checkedReferenced;

	final BasicQueue<RecImpl> checkedUnreachable;

	final int limit;

	double rewriteProbability;

	TaskState state;

	int stChangedSlot, stUnreferenced, stDropped;

	TaskLocalBatchProcessReferenced(//
			final TaskState state,
			final int limit,
			final BasicQueue<RecImpl> checkedReferenced,
			final BasicQueue<RecImpl> checkedUnreachable,
			final TaskCommon<?> callback) {

		this.rewriteProbability = 1.0;
		this.state = state;
		this.limit = limit;
		this.checkedReferenced = checkedReferenced;
		this.checkedUnreachable = checkedUnreachable;
		this.callback = callback;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, ?> xct = context.createNewWorkerTransaction();
		try {
			for (int loops = this.limit;;) {
				if (--loops == 0) {
					StorageImplS4.log(EventLevel.DEBUG, "SCHEDULE(" + context.local.scheduleTicks + ")", "LIFECYCLE-CHECK-BATCH-PROCESS-CHECKED", "requeued (yield), this=" + this);
					if (xct != null) {
						xct.commit();
						xct = null;
					}
					context.enqueue(this);
					return;
				}
				final RecImpl recordDead = this.checkedUnreachable.pollFirst();
				final RecImpl recordKeep = this.checkedReferenced.pollFirst();
				if (recordDead == null && recordKeep == null) {
					if (xct != null) {
						xct.commit();
						xct = null;
					}
					context.enqueue(this.callback);
					this.setResult(null);
					return;
				}
				if (recordDead != null) {
					switch (this.state) {
						case EXEC_FRESH :
							xct.arsRecordDelete(recordDead);
							++this.stDropped;
							break;
						case EXEC_REFERENCED :
						case EXEC_INVALID :
							/** make it FRESH */
							recordDead.scheduleBits = context.local.createScheduleFresh();
							xct.arsRecordUpsert(recordDead);
							++this.stUnreferenced;
							break;
						default :
					}
				}
				if (recordKeep != null) {
					final short oldBits = recordKeep.scheduleBits;
					final short newBits;

					// assignment in condition
					if (oldBits != (newBits = context.local.rootRecord.guid.equals(recordKeep.guid)
						// fix the 'root' record
						? S4ScheduleType.T07_ROOT.baseOffset()
						// make new bits
						: context.local.createScheduleReferenced(oldBits, this.rewriteProbability)//
					)) {
						recordKeep.scheduleBits = newBits;
						xct.arsRecordUpsert(recordKeep);
						++this.stChangedSlot;
					}
				}
			}
		} finally {
			if (xct != null) {
				xct.cancel();
			}
		}
	}

	@Override
	public String toString() {

		return "TASK_BATCH_PROCESS_CHECKED[hasRemaining=" //
				+ (this.checkedReferenced.hasNext() || this.checkedUnreachable.hasNext()) //
				+ ", callback=" + this.callback //
				+ ", stCount=" + (this.stChangedSlot + this.stUnreferenced + this.stDropped) //
				+ "]"//
		;
	}
}
