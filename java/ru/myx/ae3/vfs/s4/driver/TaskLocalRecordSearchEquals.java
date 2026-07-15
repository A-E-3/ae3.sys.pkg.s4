package ru.myx.ae3.vfs.s4.driver;

import java.util.function.Function;

import ru.myx.ae3.common.Value;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.vfs.s4.common.ArrRecImpl;
import ru.myx.ae3.vfs.s4.common.ArrRefImpl;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

final class TaskLocalRecordSearchEquals extends TaskCommon<ArrRecImpl<RecImpl>> implements Function<Value<RecImpl>, Void> {

	private static final int LONG_ITERATION = 1000;

	private static final int LONG_THRESHOLD = 3000;

	private RecImpl searchValue;

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
		this.searchValue = keyStart;
		this.leftTotal = limit;
		this.result = new ArrRecImpl<>();
	}

	@Override
	public Void apply(final Value<RecImpl> record) {

		assert record != null : "reference is null!";
		assert record.driver == null : "implementation must not set 'driver' field";
		assert record.collection == null : "implementation must not set 'collection' field";
		assert record.key != null : "record exists but key is NULL";
		assert record.mode != null : "record exists but mode is NULL";
		assert record.value != null : "record exists but target is NULL";
		record.driver = this.local;
		record.collection = this.record;
		if (--this.leftCurrent == 0) {
			this.searchValue = record.getKey();
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
				this.searchValue == null
					? null
					: this.searchValue.guid,
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
