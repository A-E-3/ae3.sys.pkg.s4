package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

/** @author myx */
final class TaskLocalGetTail //
		extends
			TaskCommon<TransferCopier> {

	private final RecImpl record;

	private final long started = System.currentTimeMillis();

	TaskLocalGetTail(final RecImpl record) {

		assert record != null : "GetTail - NULL source!";
		assert record.getClass() != RecInline.class : "GetTail - INLINE source!";
		this.record = record;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, ?> xct = context.createGlobalCommonTransaction();
		final byte[] tail = xct.readRecordTail(this.record);
		this.setResult(
				tail == null
					? null
					: Transfer.wrapCopier(tail));
	}

	@Override
	public String toString() {

		return "TASK_GET_TAIL(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): " //
				+ this.record.guid//
		;
	}

}
