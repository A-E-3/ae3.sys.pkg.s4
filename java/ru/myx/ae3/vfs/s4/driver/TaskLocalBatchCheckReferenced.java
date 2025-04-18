package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.util.BasicQueue;

final class TaskLocalBatchCheckReferenced //
		extends
			TaskCommon<Void> {

	/** splits references already cached in memory from ones to be checked in database **/
	static final int preprocessCheckCached(//
			final S4WorkerContext context,
			final BasicQueue<RecImpl> scheduled,
			final BasicQueue<RecImpl> pending,
			final BasicQueue<RecImpl> referenced) {

		int count = 0;
		for (RecImpl record; (record = scheduled.pollFirst()) != null;) {
			++count;
			if (context.local.recordsByGuid.containsKey(record.guid)) {
				referenced.offerLast(record);
			} else //
			if (context.local.rootRecord.guid.equals(record.guid)) {
				referenced.offerLast(record);
			} else {
				pending.offerLast(record);
			}
		}
		return count;
	}

	final TaskCommon<?> callback;

	final BasicQueue<RecImpl> checkedReferenced;

	final BasicQueue<RecImpl> checkedUnreachable;

	final int limit;

	final BasicQueue<RecImpl> pending;

	final double rewriteProbability;

	TaskLocalBatchCheckReferenced(//
			final BasicQueue<RecImpl> pending,
			final int limit,
			final BasicQueue<RecImpl> checkedReferenced,
			final double rewriteProbability,
			final BasicQueue<RecImpl> checkedUnreachable,
			final TaskCommon<?> callback) {

		this.pending = pending;
		this.limit = limit;
		this.checkedReferenced = checkedReferenced;
		this.rewriteProbability = rewriteProbability;
		this.checkedUnreachable = checkedUnreachable;
		this.callback = callback;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		context.worker.readCheckReferenced(//
				this.pending,
				this.checkedReferenced,
				this.checkedUnreachable//
		);
		context.enqueue(this.callback);
		this.setResult(null);
	}

	@Override
	public String toString() {

		return "TASK_BATCH_CHECK_REFERENCED[callback=" + this.callback + "]";
	}
}
