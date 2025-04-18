package ru.myx.ae3.vfs.s4.driver;

import java.util.function.Function;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.ae3.vfs.s4.common.ArrImpl;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

final class TaskLocalGetLinksRange //
		extends
			TaskCommon<ArrImpl<RefImpl<RecImpl>>>
		implements
			Function<RefImpl<RecImpl>, Void> {
	
	private static final int LONG_ITERATION = 1000;
	
	private static final int LONG_THRESHOLD = 3000;
	
	/** can do separate class for this field to minimize objects in memory */
	private final boolean backwards;
	
	private RecImpl keyStart;
	
	private final RecImpl keyStop;
	
	private int leftCurrent;
	
	private int leftTotal;
	
	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> local;
	
	private final TreeReadType mode;
	
	private final RecImpl record;
	
	private final ArrImpl<RefImpl<RecImpl>> result;
	
	private final long started = System.currentTimeMillis();
	
	TaskLocalGetLinksRange(//
			final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> local,
			final RecImpl record,
			final RecImpl keyStart,
			final RecImpl keyStop,
			final int limit,
			final boolean backwards,
			final TreeReadType mode) {
		
		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "GetLinks - INLINE source!";
		this.local = local;
		this.record = record;
		this.keyStart = keyStart;
		this.keyStop = keyStop;
		this.leftTotal = limit;
		this.backwards = backwards;
		this.mode = mode;
		this.result = new ArrImpl<>();
	}
	
	@Override
	public Void apply(final RefImpl<RecImpl> reference) {
		
		assert reference != null : "reference is null!";
		assert reference.driver == null : "implementation must not set 'driver' field";
		assert reference.collection == null : "implementation must not set 'collection' field";
		assert reference.key != null : "record exists but key is NULL";
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
		
		final int leftTotal = this.leftTotal;
		final int limit;
		if (leftTotal == 0 || leftTotal > TaskLocalGetLinksRange.LONG_THRESHOLD) {
			limit = this.leftCurrent = TaskLocalGetLinksRange.LONG_ITERATION + 1;
		} else {
			limit = leftTotal;
			this.leftCurrent = 0;
		}
		
		final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, ?> xct = context.createGlobalCommonTransaction();
		final int count = xct.readContainerContentsRange( //
				this,
				this.record,
				this.keyStart == null
					? null
					: this.keyStart.guid,
				this.keyStop == null
					? null
					: this.keyStop.guid,
				limit,
				this.backwards//
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
	
	@Override
	public String toString() {
		
		return "TASK_GET_LINKS_RANGE(" + Format.Compact.toPeriod(System.currentTimeMillis() - this.started) + "): " //
				+ this.record //
				+ ", start=" + this.keyStart //
				+ ", stop=" + this.keyStop //
				+ ", leftTotal=" + this.leftTotal //
				+ ", backwards=" + this.backwards //
				+ ", mode=" + this.mode//
		;
	}
}
