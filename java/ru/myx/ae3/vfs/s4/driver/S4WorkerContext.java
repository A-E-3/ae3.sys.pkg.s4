package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.Engine;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.impl.S4TreeDriver;
import ru.myx.ae3.vfs.s4.impl.S4TreeWorker;

/** @author myx */
public final class S4WorkerContext {
	
	/**
	 *
	 */
	public S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> driver;
	
	/**
	 *
	 */
	public S4DriverAbstract local;
	
	/**
	 *
	 */
	public S4TreeWorker<RecImpl, RefImpl<RecImpl>, Object> worker;
	
	/** @param local */
	public S4WorkerContext(final S4DriverAbstract local) {
		
		this.local = local;
		this.driver = local.driver;
		this.worker = this.driver.createWorker();
	}
	
	/** @param guid
	 * @param task */
	protected final void cacheRecord(final Guid guid, final Value<RecImpl> task) {
		
		S4DriverAbstract.CACHE_RECORD_MODE.create(this.local, guid, task);
	}
	
	/** @return */
	public S4WorkerInterface<RecImpl, RefImpl<RecImpl>, Object> createGlobalCommonTransaction() {
		
		final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, Object> result = this.worker.createGlobalCommonTransaction();
		if (result == null) {
			throw new UnsupportedOperationException("Worker (" + this.worker.getClass().getName() + ") must support global transaction!");
		}
		return result;
	}
	
	/** @return */
	public S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, Object> createNewWorkerTransaction() {
		
		final S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, Object> result = this.worker.createNewWorkerTransaction();
		if (result == null) {
			throw new UnsupportedOperationException("Worker (" + this.worker.getClass().getName() + ") must support worker transaction!");
		}
		return result;
	}
	
	/** @param xct
	 * @param rec
	 * @throws Exception */
	protected void doDefineFreshRecord(final S4WorkerInterface<RecImpl, RefImpl<RecImpl>, ?> xct, final RecImpl rec) throws Exception {
		
		rec.scheduleBits = this.local.createScheduleFresh();
		xct.arsRecordUpsert(rec);
	}
	
	/** @param task */
	protected void enqueue(final TaskCommon<?> task) {
		
		this.local.enqueue(task);
	}
	
	/** @param guid
	 * @return */
	protected final Value<RecImpl> getCachedRecordReference(final Guid guid) {
		
		final CacheRecord cache = this.local.recordsByGuid.get(guid);
		if (cache == null) {
			return null;
		}
		return cache.get();
	}
}
