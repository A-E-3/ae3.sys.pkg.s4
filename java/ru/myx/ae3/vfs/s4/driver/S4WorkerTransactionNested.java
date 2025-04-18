package ru.myx.ae3.vfs.s4.driver;

import java.util.Collection;
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
public class S4WorkerTransactionNested<O extends RecImpl, R extends RefImpl<O>, L extends Object> //
		implements
			S4WorkerTransaction<O, R, L> {
	
	private final S4WorkerInterface<O, R, L> iface;
	
	/** @param iface */
	public S4WorkerTransactionNested(final S4WorkerInterface<O, R, L> iface) {
		
		this.iface = iface;
	}
	
	@Override
	public void arsRecordDelete(final O record) throws Exception {
		
		this.iface.arsRecordDelete(record);
	}
	
	@Override
	public void arsRecordUpsert(final O record) throws Exception {
		
		this.iface.arsRecordUpsert(record);
	}
	
	@Override
	public void cancel() throws Exception {
		
		//
	}
	
	@Override
	public void commit() throws Exception {
		
		//
	}
	
	@Override
	public void arsLinkDelete(final O container, final Guid key, final TreeLinkType mode, final long modified) throws Exception {
		
		this.iface.arsLinkDelete(container, key, mode, modified);
	}
	
	@Override
	public void arsLinkUpdate(final O container, final O newContainer, final Guid key, final Guid newKey, final TreeLinkType mode, final long modified, final Guid value)
			throws Exception {
		
		this.iface.arsLinkUpdate(container, newContainer, key, newKey, mode, modified, value);
	}
	
	@Override
	public void arsLinkUpsert(final O container, final Guid key, final TreeLinkType mode, final long modified, final Guid value) throws Exception {
		
		this.iface.arsLinkUpsert(container, key, mode, modified, value);
	}
	
	@Override
	public int readContainerContentsRange(final Function<R, ?> target, final O container, final Guid keyStart, final Guid keyStop, final int limit, final boolean backwards)
			throws Exception {
		
		return this.iface.readContainerContentsRange(target, container, keyStart, keyStop, limit, backwards);
	}
	
	@Override
	public R readContainerElement(final O container, final Guid key) throws Exception {
		
		return this.iface.readContainerElement(container, key);
	}
	
	@Override
	public O readRecord(final Guid guid) throws Exception {
		
		return this.iface.readRecord(guid);
	}
	
	@Override
	public byte[] readRecordTail(final O record) throws Exception {
		
		return this.iface.readRecordTail(record);
	}
	
	@Override
	public int searchBetween(final Function<L, ?> target, final Guid key, final Guid value1, final Guid value2, final int limit) throws Exception {
		
		return this.iface.searchBetween(target, key, value1, value2, limit);
	}
	
	@Override
	public int searchEquals(final Collection<L> target, final Guid key, final Guid value, final int limit) throws Exception {
		
		return this.iface.searchEquals(target, key, value, limit);
	}
}
