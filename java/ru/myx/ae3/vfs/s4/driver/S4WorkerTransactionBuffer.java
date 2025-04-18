package ru.myx.ae3.vfs.s4.driver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;

/** TODO: unfinished / unused
 *
 * @author myx
 * @param <O>
 * @param <R>
 * @param <L> */
public class S4WorkerTransactionBuffer<O extends RecImpl, R extends RefImpl<O>, L extends Object> //
		implements
			S4WorkerTransaction<O, R, L> {

	private final S4WorkerTransaction<O, R, L> parent;
	private final Map<O, O> recordUpdates;

	/** @param parent */
	public S4WorkerTransactionBuffer(final S4WorkerTransaction<O, R, L> parent) {

		this.parent = parent;
		this.recordUpdates = new HashMap<>();
	}

	@Override
	public void arsRecordDelete(final O record) throws Exception {

		// this.parent.arsRecordDelete(record);
		this.recordUpdates.put(record, null);
	}

	@Override
	public void arsRecordUpsert(final O record) throws Exception {

		// this.parent.arsRecordUpsert(record);
		this.recordUpdates.put(record, record);
	}

	@Override
	public void cancel() throws Exception {

		//
		this.recordUpdates.clear();
		this.parent.cancel();
	}

	@Override
	public void commit() throws Exception {
		
		//
		final Set<O> recordsDelete = new TreeSet<>();
		final Set<O> recordsUpsert = new TreeSet<>();
		for (final Map.Entry<O, O> entry : this.recordUpdates.entrySet()) {
			final O record = entry.getKey();
			final O value = entry.getValue();
			(null == value
				? recordsDelete
				: recordsUpsert).add(record)//
			;
		}
		
		this.parent.arsRecordsDelete(recordsDelete);
		this.parent.arsRecordsUpsert(recordsUpsert);

		this.parent.commit();
		
		this.recordUpdates.clear();
	}

	@Override
	public void arsLinkDelete(final O container, final Guid key, final TreeLinkType mode, final long modified) throws Exception {

		this.parent.arsLinkDelete(container, key, mode, modified);
	}

	@Override
	public void arsLinkUpdate(final O container, final O newContainer, final Guid key, final Guid newKey, final TreeLinkType mode, final long modified, final Guid value)
			throws Exception {

		this.parent.arsLinkUpdate(container, newContainer, key, newKey, mode, modified, value);
	}

	@Override
	public void arsLinkUpsert(final O container, final Guid key, final TreeLinkType mode, final long modified, final Guid value) throws Exception {

		this.parent.arsLinkUpsert(container, key, mode, modified, value);
	}

	@Override
	public int readContainerContentsRange(final Function<R, ?> target, final O container, final Guid keyStart, final Guid keyStop, final int limit, final boolean backwards)
			throws Exception {

		return this.parent.readContainerContentsRange(target, container, keyStart, keyStop, limit, backwards);
	}

	@Override
	public R readContainerElement(final O container, final Guid key) throws Exception {

		return this.parent.readContainerElement(container, key);
	}

	@Override
	public O readRecord(final Guid guid) throws Exception {

		return this.parent.readRecord(guid);
	}

	@Override
	public byte[] readRecordTail(final O record) throws Exception {

		return this.parent.readRecordTail(record);
	}

	@Override
	public int searchBetween(final Function<L, ?> target, final Guid key, final Guid value1, final Guid value2, final int limit) throws Exception {

		return this.parent.searchBetween(target, key, value1, value2, limit);
	}

	@Override
	public int searchEquals(final Collection<L> target, final Guid key, final Guid value, final int limit) throws Exception {

		return this.parent.searchEquals(target, key, value, limit);
	}
}
