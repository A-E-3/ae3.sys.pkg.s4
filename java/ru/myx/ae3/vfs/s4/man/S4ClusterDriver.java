package ru.myx.ae3.vfs.s4.man;

import java.io.File;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.ae3.vfs.s4.common.ArrImpl;
import ru.myx.ae3.vfs.s4.common.ArsTransactionS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.common.RepairChecker;
import ru.myx.ae3.vfs.s4.common.S4StoreType;
import ru.myx.ae3.vfs.s4.driver.S4Driver;
import ru.myx.ae3.vfs.s4.lcl.S4LocalDriver;
import ru.myx.ae3.vfs.s4.net.S4Network;

/** @author myx */
public class S4ClusterDriver //
		implements
			S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> {

	/** WARNING, casted to this type - use only instances provided by implementation. */
	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> cacheLocal;

	private final File extraFolder;

	private final File[] extraLeafs;

	private final S4ClusterDriver loopback;

	/** WARNING, casted to this type - use only instances provided by implementation. */
	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> nextLocal;

	private final S4Network nextNetwork;

	private final long pointNext;

	private final long pointOwn;

	private final long pointPrev;

	/** WARNING, casted to this type - use only instances provided by implementation. */
	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> prevLocal;

	private final S4Network prevNetwork;

	private long rootInitialized;

	private RecImpl rootRecord;

	private long rootSynchronized;

	/** @throws Exception */
	public S4ClusterDriver() throws Exception {

		final File folder = new File(Engine.PATH_PRIVATE, "data");
		System.out.println("S4MAN: private data folder path: " + folder.getAbsolutePath());
		folder.mkdirs();
		this.extraFolder = new File(folder, "extra");
		this.extraFolder.mkdirs();
		this.extraLeafs = new File[256];
		for (int i = 255; i > 15; --i) {
			this.extraLeafs[i] = new File(this.extraFolder, Integer.toHexString(i).toUpperCase());
		}
		for (int i = 15; i >= 0; --i) {
			this.extraLeafs[i] = new File(this.extraFolder, '0' + Integer.toHexString(i).toUpperCase());
		}

		this.nextLocal = new S4LocalDriver(S4StoreType.NEXT);
		this.prevLocal = new S4LocalDriver(S4StoreType.PREV);
		this.cacheLocal = new S4LocalDriver(S4StoreType.CACHE);

		// everything "NEXT"
		this.pointPrev = 0x00000000L;
		this.pointOwn = 0x00000000L;
		this.pointNext = 0xFFFFFFFFL;

		this.nextNetwork = null;
		this.prevNetwork = null;

		this.loopback = new S4ClusterDriver(this);
	}

	private S4ClusterDriver(final S4ClusterDriver parent) {

		this.extraFolder = parent.extraFolder;
		this.extraLeafs = parent.extraLeafs;

		this.nextLocal = parent.prevLocal;
		this.prevLocal = parent.nextLocal;
		this.cacheLocal = parent.cacheLocal;

		// everything "PREV"
		this.pointPrev = 0xFFFFFFFFL;
		this.pointOwn = 0xFFFFFFFFL;
		this.pointNext = 0xFFFFFFFFL;

		this.nextNetwork = null;
		this.prevNetwork = null;

		this.loopback = parent;
	}

	@Override
	public RecImpl createBinaryTemplate(final TransferCopier copier) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RepairChecker createCheckContext(final BaseObject properties) {

		return null;
	}

	@Override
	public RecImpl createContainerTemplate() {

		throw new UnsupportedOperationException();
	}

	@Override
	public RecImpl createPrimitiveTemplate(final Guid guid) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RecImpl createTextTemplate(final TransferCopier copier) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ArsTransactionS4 createUnderlyingTransaction() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<RefImpl<RecImpl>> doLinkMoveRename(final RefImpl<RecImpl> template,
			final RecImpl record,
			final RecImpl key,
			final RecImpl newRecord,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl value) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<RefImpl<RecImpl>> doLinkRename(final RefImpl<RecImpl> template,
			final RecImpl record,
			final RecImpl key,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl value) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<RefImpl<RecImpl>>
			doLinkSet(final RefImpl<RecImpl> template, final RecImpl record, final RecImpl key, final TreeLinkType mode, final long modified, final RecImpl value) {

		throw new UnsupportedOperationException();
	}

	@Override
	public String getKey() {

		return "cluster";
	}

	@Override
	public Value<RefImpl<RecImpl>> getLink(final RecImpl record, final RecImpl key, final TreeLinkType mode) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<ArrImpl<RefImpl<RecImpl>>> getLinks(final RecImpl record, final TreeReadType mode) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<ArrImpl<RefImpl<RecImpl>>>
			getLinksRange(final RecImpl record, final RecImpl keyStart, final RecImpl keyStop, final int limit, final boolean backwards, final TreeReadType mode) {

		throw new UnsupportedOperationException();
	}

	/** Get Record implementation for GUID
	 *
	 * @param guid
	 * @return */
	@Override
	public Value<RecImpl> getRecordByGuid(final Guid guid) {

		if (guid == null || !guid.isValid()) {
			return null;
		}
		if (guid.isInline()) {
			return null;
		}
		final long point = guid.hashCode() & 0xFFFFFFFFL;
		if (this.pointOwn != this.pointNext && (this.pointOwn < this.pointNext
			? this.pointOwn < point && point <= this.pointNext
			: this.pointOwn < point || point <= this.pointNext)) {
			return this.nextLocal.getRecordByGuid(guid);
		}
		if (this.pointPrev != this.pointOwn && (this.pointPrev < this.pointOwn
			? this.pointPrev < point && point <= this.pointOwn
			: this.pointPrev < point || point <= this.pointOwn)) {
			return this.prevLocal.getRecordByGuid(guid);
		}
		{ // out of my zone!
			return this.cacheLocal.getRecordByGuid(guid);
		}
	}

	@Override
	public Value<TransferCopier> getRecordTail(final RecImpl record) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RefImpl<RecImpl> getRootReference() {

		throw new UnsupportedOperationException();
	}

	/** @throws Exception */
	@Override
	public boolean start() throws Exception {

		this.nextLocal.start();
		this.prevLocal.start();
		this.cacheLocal.start();
		return true;
	}

	/** @throws Exception */
	@Override
	public boolean stop() throws Exception {

		try {
			Thread.sleep(1000L);
			System.out.println("S4IMPL: MANAGER: running finalization...");
			Runtime.getRuntime().runFinalization();
			Thread.sleep(1000L);
			System.out.println("S4IMPL: MANAGER: running garbage collection...");
			Runtime.getRuntime().gc();
			Thread.sleep(1000L);
		} catch (final Throwable e) {
			// ignore
		}

		this.cacheLocal.stop();
		this.prevLocal.stop();
		this.nextLocal.stop();
		return true;
	}

	@Override
	public String toString() {

		return "S4CLUSTER";
	}
}
