package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.common.Value;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.s4.common.RecImpl;

final class TaskLocalGetRecord //
		extends
			TaskCommon<RecImpl> {
	
	private final Guid guid;
	
	private final long started = System.currentTimeMillis();
	
	TaskLocalGetRecord(final Guid guid) {
		
		this.guid = guid;
	}
	
	@Override
	protected void execute(final S4WorkerContext context) throws Exception {
		
		final Value<RecImpl> cached = context.getCachedRecordReference(this.guid);
		if (cached != null) {
			if (cached instanceof final TaskCommon<RecImpl> cachedTask) {
				this.setDuplicateOf(cachedTask);
				return;
			}
			final RecImpl cachedValue = cached.baseValue();
			if (cachedValue != null) {
				this.setResult(cachedValue);
				return;
			}
		}
		
		context.cacheRecord(this.guid, this);
		final S4WorkerInterface<?, ?, ?> txn = context.createGlobalCommonTransaction();
		final RecImpl record = txn.readRecord(this.guid);
		if (record != null) {
			assert (record.runtimeState & RecImpl.RT_TEMPLATE) == 0 : "Usage of system-reserved bits!";
			record.runtimeState &= ~RecImpl.RT_TEMPLATE;
		}
		this.setResult(record);
	}
	
	@Override
	public String toString() {
		
		return "TASK_GET_RECORD(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): "//
				+ this.guid//
		;
	}
	
}
