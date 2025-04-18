package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

class TaskLocalLinkMoveRename //
		extends
			TaskCommon<RefImpl<RecImpl>> {
	
	private final RecImpl key;
	
	private final TreeLinkType mode;
	
	private final long modified;
	
	private final RecImpl newKey;
	
	private final RecImpl newRecord;
	
	private final RecImpl record;
	
	private final long started = System.currentTimeMillis();
	
	private final RefImpl<RecImpl> template;
	
	private final RecImpl value;
	
	public TaskLocalLinkMoveRename(
			final RefImpl<RecImpl> template,
			final RecImpl record,
			final RecImpl key,
			final RecImpl newRecord,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl value) {
		
		assert template != null : "Template shouldn't be NULL";
		this.template = template;
		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "SetLink - INLINE source!";
		this.record = record;
		assert key != null : "Key shouldn't be NULL";
		assert key.isInline() || key.getClass() != RecInline.class : "Inline record is not primitive!";
		this.key = key;
		assert newRecord != null : "Record shouldn't be NULL";
		assert newRecord.getClass() != RecInline.class : "SetLink - INLINE source!";
		this.newRecord = newRecord;
		assert newKey != null : "newKey shouldn't be NULL";
		assert newKey.isInline() || newKey.getClass() != RecInline.class : "Inline record is not primitive!";
		this.newKey = newKey;
		this.mode = mode;
		this.modified = modified;
		assert value != null : "Target value is NULL!";
		this.value = value;
	}
	
	@Override
	protected void execute(final S4WorkerContext context) throws Exception {
		
		S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, ?> xct = context.createNewWorkerTransaction();
		
		try {
			{
				final RecImpl value = this.value;
				if ((value.runtimeState & RecImpl.RT_TEMPLATE) != 0) {
					value.scheduleBits = context.local.createScheduleFresh();
					xct.arsRecordUpsert(value);
					value.runtimeState &= ~RecImpl.RT_TEMPLATE;
					context.cacheRecord(value.guid, value);
				}
			}
			{
				final RecImpl value = this.newKey;
				if ((value.runtimeState & RecImpl.RT_TEMPLATE) != 0) {
					value.scheduleBits = context.local.createScheduleFresh();
					xct.arsRecordUpsert(value);
					value.runtimeState &= ~RecImpl.RT_TEMPLATE;
					context.cacheRecord(value.guid, value);
				}
			}
			
			xct.arsLinkUpdate(//
					this.record,
					this.newRecord,
					this.key.guid,
					this.newKey.guid,
					this.mode,
					this.modified,
					this.value.guid//
			);

			this.template.collection = this.newRecord;
			this.template.driver = context.local;
			this.template.key = this.newKey.guid;
			this.template.keyRecord = this.newKey;
			this.template.mode = this.mode;
			this.template.modified = this.modified;
			this.template.value = this.value.guid;
			this.template.valueRecord = this.value;
			
			xct.commit();
			xct = null;
		} finally {
			if (xct != null) {
				xct.cancel();
			}
		}
		
		this.setResult(this.template);
	}
	
	@Override
	public String toString() {
		
		return "TASK_LINK_MOVE_RENAME(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): "//
				+ this.record.guid //
				+ ", key=" + this.key //
				+ (this.record == this.newRecord
					? ""
					: ", newRecord=" + this.newRecord) //
				+ (this.key == this.newKey
					? ""
					: ", newKey=" + this.newKey)//
		;
	}
}
