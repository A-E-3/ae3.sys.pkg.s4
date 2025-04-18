package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

final class TaskLocalGetLink //
		extends
			TaskCommon<RefImpl<RecImpl>> {

	private final RecImpl key;

	private final TreeLinkType mode;

	private final RecImpl record;

	private final long started = System.currentTimeMillis();

	TaskLocalGetLink(//
			final RecImpl record,
			final RecImpl key,
			final TreeLinkType mode) {

		if (record == null) {
			assert false : "GetLink - NULL source!";
			throw new NullPointerException("GetLink, NULL record!");
		}
		assert record.getClass() != RecInline.class : "GetLink - INLINE source!";
		this.record = record;
		this.key = key;
		this.mode = mode;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		final RefImpl<RecImpl> reference;
		final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, ?> txn = context.createGlobalCommonTransaction();
		{
			final RefImpl<RecImpl> existent = txn.readContainerElement(this.record, this.key.guid);
			if (existent == null) {
				if (this.mode == null) {
					this.setResult(null);
					return;
				}
				final RefImpl<RecImpl> created = context.driver.createReferenceTemplate();
				reference = created;
				reference.mode = this.mode;
			} else {
				reference = existent;
				assert reference.driver == null : "implementation must not set 'driver' field";
				assert reference.collection == null : "implementation must not set 'collection' field";
				assert reference.key != null : "record exists but mode is NULL";
				assert reference.mode != null : "record exists but mode is NULL";
				assert reference.value != null : "record exists but target is NULL";
			}
		}
		reference.driver = context.local;
		reference.collection = this.record;
		reference.key = this.key.guid;
		reference.keyRecord = this.key;
		this.setResult(reference);
	}

	@Override
	public String toString() {

		return "TASK_GET_LINK(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): " //
				+ this.record.guid //
				+ ", key=" + this.key//
		;
	}

}
