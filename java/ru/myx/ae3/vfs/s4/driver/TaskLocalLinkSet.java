package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

class TaskLocalLinkSet //
		extends
			TaskCommon<RefImpl<RecImpl>> {

	private final RecImpl key;

	private final TreeLinkType mode;

	private final long modified;

	private final RecImpl record;

	private final long started = System.currentTimeMillis();

	private final RefImpl<RecImpl> template;

	private final RecImpl value;

	public TaskLocalLinkSet(//
			final RefImpl<RecImpl> template,
			final RecImpl record,
			final RecImpl key,
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
		this.mode = mode;
		this.modified = modified;
		assert value == null || value.isInline() || value.getClass() != RecInline.class : "Inline record is not primitive!";
		this.value = value;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, ?> xct = context.createNewWorkerTransaction();

		try {
			if (this.value != null) {
				final RecImpl value = this.value;
				if ((value.runtimeState & RecImpl.RT_TEMPLATE) != 0) {
					value.scheduleBits = context.local.createScheduleFresh();
					xct.arsRecordUpsert(value);
					value.runtimeState &= ~RecImpl.RT_TEMPLATE;
					context.cacheRecord(value.guid, value);
				}
			}
			{
				final RecImpl value = this.key;
				if ((value.runtimeState & RecImpl.RT_TEMPLATE) != 0) {
					value.scheduleBits = context.local.createScheduleFresh();
					xct.arsRecordUpsert(value);
					value.runtimeState &= ~RecImpl.RT_TEMPLATE;
					context.cacheRecord(value.guid, value);
				}
			}
			final Guid valueGuid = this.value == null
				? null
				: this.value.guid;

			xct.arsLinkUpsert(this.record, this.key.guid, this.mode, this.modified, valueGuid);

			this.template.collection = this.record;
			this.template.driver = context.local;
			this.template.key = this.key.guid;
			this.template.keyRecord = this.key;
			this.template.mode = this.mode;
			this.template.modified = this.modified;
			this.template.value = valueGuid;
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

		return "TASK_LINK_SET(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): " //
				+ this.record.guid //
				+ ", key=" + this.key//
		;
	}
}
