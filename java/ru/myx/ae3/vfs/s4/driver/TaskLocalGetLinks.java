package ru.myx.ae3.vfs.s4.driver;

import java.util.function.Function;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.ae3.vfs.s4.common.ArrImpl;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

final class TaskLocalGetLinks //
		extends
			TaskCommon<ArrImpl<RefImpl<RecImpl>>>
		implements
			Function<RefImpl<RecImpl>, Void> {

	private static final int ITERATION_SIZE = 1000;

	private RecImpl keyStart;

	private int leftCurrent;

	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> local;

	private final TreeReadType mode;

	private final RecImpl record;

	private final ArrImpl<RefImpl<RecImpl>> result;

	private final long started = System.currentTimeMillis();

	TaskLocalGetLinks(//
			final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> local,
			final RecImpl record,
			final TreeReadType mode) {

		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "GetLinks - INLINE source!";
		this.local = local;
		this.record = record;
		this.mode = mode;
		this.result = new ArrImpl<>();
	}

	@Override
	public Void apply(final RefImpl<RecImpl> reference) {

		assert reference != null : "reference is null!";
		assert reference.driver == null : "implementation must not set 'driver' field";
		assert reference.collection == null : "implementation must not set 'collection' field";
		assert reference.key != null : "record exists but mode is NULL";
		assert reference.mode != null : "record exists but mode is NULL";
		assert reference.value != null : "record exists but target is NULL";
		reference.driver = this.local;
		reference.collection = this.record;
		if (--this.leftCurrent == 0) {
			this.keyStart = reference.getKey();
		} else {
			this.result.add(reference);
		}
		return null;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		final int limit = this.leftCurrent = TaskLocalGetLinks.ITERATION_SIZE;
		final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, ?> xct = context.createGlobalCommonTransaction();

		final int count = xct.readContainerContentsRange(//
				this,
				this.record,
				this.keyStart == null
					? null
					: this.keyStart.guid,
				null,
				limit,
				false//
		);

		if (count < limit) {
			/** it's over */
			this.setResult(this.result);
			return;
		}
		/** again */
		context.enqueue(this);
	}

	@Override
	public String toString() {

		return "TASK_GET_LINKS(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): " //
				+ this.record //
				+ ", mode=" + this.mode//
		;
	}
}
