package ru.myx.ae3.vfs.s4.driver;

import java.util.function.Function;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.ae3.vfs.s4.common.ArrRecImpl;
import ru.myx.ae3.vfs.s4.common.ArrRefImpl;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

final class TaskLocalRecordSearchBetween extends TaskCommon<ArrRecImpl<RecImpl>> implements Function<RecImpl, Void> {

	private static final int LONG_ITERATION = 1000;

	private static final int LONG_THRESHOLD = 3000;

	private Guid searchStart;

	private final Guid searchStop;

	private int leftCurrent;

	private int leftTotal;

	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrRefImpl<RefImpl<RecImpl>>, Object> local;

	private final TreeReadType mode;

	private final RecImpl record;

	private final ArrRecImpl<RecImpl> result;

	private final long started = System.currentTimeMillis();

	TaskLocalRecordSearchBetween(//
			final S4Driver<RecImpl, RefImpl<RecImpl>, ArrRefImpl<RefImpl<RecImpl>>, Object> local,
			final RecImpl record,
			final RecImpl keyStart,
			final RecImpl keyStop,
			final int limit,
			final TreeReadType mode) {

		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "GetLinks - INLINE source!";
		this.local = local;
		this.record = record;
		this.searchStart = keyStart == null
			? null
			: keyStart.guid;
		this.searchStop = keyStop == null
			? null
			: keyStop.guid;
		this.leftTotal = limit;
		this.mode = mode;
		this.result = new ArrRecImpl<>();
	}

	/** Note: 'record.guid' here is NOT the record's real identity - on this search-index lookup
	 * path it carries the matched index value instead, see
	 * {@link S4WorkerInterface#searchBetween}. */
	@Override
	public Void apply(final RecImpl record) {

		assert record != null : "record is null!";
		if (--this.leftCurrent == 0) {
			this.searchStart = record.guid;
		} else {
			this.result.add(record);
		}
		return null;
	}

	@Override
	public String toString() {

		return "TASK_SEARCH_BETWEEN(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): " //
				+ this.record //
				+ ", start=" + this.searchStart //
				+ ", stop=" + this.searchStop //
				+ ", leftTotal=" + this.leftTotal //
				+ ", mode=" + this.mode//
		;
	}

	@Override
	protected void execute(final S4WorkerContext context) throws Exception {

		final int leftTotal = this.leftTotal;
		final int limit;
		if (leftTotal == 0 || leftTotal > TaskLocalRecordSearchBetween.LONG_THRESHOLD) {
			limit = this.leftCurrent = TaskLocalRecordSearchBetween.LONG_ITERATION + 1;
		} else {
			limit = leftTotal;
			this.leftCurrent = 0;
		}

		final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, ?> xct = context.createGlobalCommonTransaction();
		final int count = xct.searchBetween(
				this,
				this.record.guid,
				this.searchStart,
				this.searchStop,
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
