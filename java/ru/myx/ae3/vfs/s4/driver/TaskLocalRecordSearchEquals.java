package ru.myx.ae3.vfs.s4.driver;

import java.util.function.Function;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.s4.common.ArrRecImpl;
import ru.myx.ae3.vfs.s4.common.ArrRefImpl;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

final class TaskLocalRecordSearchEquals extends TaskCommon<ArrRecImpl<RecImpl>> implements Function<RecImpl, Void> {

	private static final int LONG_ITERATION = 1000;

	private static final int LONG_THRESHOLD = 3000;

	private Guid searchValue;

	private int leftCurrent;

	private int leftTotal;

	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrRefImpl<RefImpl<RecImpl>>, Object> local;

	private final RecImpl record;

	private final ArrRecImpl<RecImpl> result;

	private final long started = System.currentTimeMillis();

	TaskLocalRecordSearchEquals(//
			final S4Driver<RecImpl, RefImpl<RecImpl>, ArrRefImpl<RefImpl<RecImpl>>, Object> local,
			final RecImpl record,
			final RecImpl keyStart,
			final int limit) {

		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "GetLinks - INLINE source!";
		this.local = local;
		this.record = record;
		this.searchValue = keyStart == null
			? null
			: keyStart.guid;
		this.leftTotal = limit;
		this.result = new ArrRecImpl<>();
	}

	/** Note: 'record.guid' here is NOT the record's real identity - on this search-index lookup
	 * path it carries the matched index value instead, see
	 * {@link S4WorkerInterface#searchEquals}. */
	@Override
	public Void apply(final RecImpl record) {

		assert record != null : "record is null!";
		if (--this.leftCurrent == 0) {
			this.searchValue = record.guid;
		} else {
			this.result.add(record);
		}
		return null;
	}

	@Override
	public String toString() {

		return "TASK_SEARCH_EQUALS(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): " //
				+ this.record //
				+ ", searchValue=" + this.searchValue //
				+ ", leftTotal=" + this.leftTotal //
		;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		final int leftTotal = this.leftTotal;
		final int limit;
		if (leftTotal == 0 || leftTotal > TaskLocalRecordSearchEquals.LONG_THRESHOLD) {
			limit = this.leftCurrent = TaskLocalRecordSearchEquals.LONG_ITERATION + 1;
		} else {
			limit = leftTotal;
			this.leftCurrent = 0;
		}

		final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, Object> xct = context.createGlobalCommonTransaction();
		final int count = xct.searchEquals( //
				this,
				this.record.guid,
				this.searchValue,
				limit//
		);

		if (count < limit || count == leftTotal) {
			/** it's over */
			this.leftTotal -= count;
			this.setResult(this.result);
			return;
		}
		/** again, last item is used for getting the next keyStart */
		this.leftTotal -= count - 1;
		context.enqueue(this);
	}
}
