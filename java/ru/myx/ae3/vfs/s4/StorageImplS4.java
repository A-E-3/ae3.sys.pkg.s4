package ru.myx.ae3.vfs.s4;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.binary.WrapCopier;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.reflect.ReflectionEnumerable;
import ru.myx.ae3.reflect.ReflectionExplicit;
import ru.myx.ae3.reflect.ReflectionManual;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.report.ReportReceiver;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.ae3.vfs.ars.ArsStorage;
import ru.myx.ae3.vfs.s4.common.ArrImpl;
import ru.myx.ae3.vfs.s4.common.ArsTransactionS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.common.RefInline;
import ru.myx.ae3.vfs.s4.common.RepairChecker;
import ru.myx.ae3.vfs.s4.common.S4StoreType;
import ru.myx.ae3.vfs.s4.driver.S4Driver;
import ru.myx.ae3.vfs.s4.lcl.S4LocalDriver;
import ru.myx.util.WeakFinalizer;

/** @author myx */
@ReflectionManual
public class StorageImplS4 //
		implements
			ArsStorage<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>> {
	
	private static final WeakHashMap<String, StorageImplS4> INSTANCES = new WeakHashMap<>();
	
	private final static ReportReceiver LOG = Report.createReceiver("ae3.s4");
	
	private static void finalizeStatic(final StorageImplS4 x) {
		
		if (x.started) {
			Report.createReceiver(null).event(x.getClass().getSimpleName(), "FINALIZE", "Storage is not closed before object is finalized!");
			try {
				x.shutdown();
			} catch (final Throwable t) {
				// ignore
			}
		}
	}
	
	/** internal, for command line utility
	 *
	 * @return */
	@ReflectionExplicit
	public static final Map<String, StorageImplS4> internGetInstances() {
		
		final Map<String, StorageImplS4> result = Create.tempMap();
		result.putAll(StorageImplS4.INSTANCES);
		return result;
	}
	
	/** @param level
	 * @param owner
	 * @param title
	 * @param message
	 * @return */
	public static boolean log(final EventLevel level, final String owner, final String title, final String message) {
		
		if (Report.MODE_DEBUG || (Report.MODE_ASSERT
			? level.ordinal() >= EventLevel.INFO.ordinal()
			: level.ordinal() > EventLevel.INFO.ordinal())) {
			return StorageImplS4.LOG.event(owner, title, message);
		}
		return true;
	}
	
	/** @param owner
	 * @param message
	 * @param t */
	public static void logError(final String owner, final String message, final Throwable t) {
		
		StorageImplS4.LOG.event(owner, "ERROR", Format.Throwable.toText(message, t));
	}
	
	private final S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> driver;
	
	private String key;
	
	private boolean started = false;
	
	{
		WeakFinalizer.register(this, StorageImplS4::finalizeStatic);
	}
	
	/** TODO: read configuration
	 *
	 * @throws Exception */
	public StorageImplS4() throws Exception {
		
		/** TODO: make lvs and vfs services */
		this.driver = new S4LocalDriver(S4StoreType.LOCAL);
		this.start();
	}
	
	/** @param driver
	 * @throws Exception */
	@ReflectionExplicit
	public StorageImplS4(final S4Driver<? extends RecImpl, ? extends RefImpl<RecImpl>, ? extends ArrImpl<RefImpl<RecImpl>>, Object> driver) throws Exception {
		
		this.driver = Convert.Any.toAny(driver);
		this.start();
	}
	
	@Override
	public RecImpl createBinaryTemplate(final TransferCopier copier) {
		
		final RecImpl result = this.driver.createBinaryTemplate(copier);
		assert Guid.forBinaryChecksum(copier).equals(result.guid) : "binary template should contain correct GUID, copierGuid=" + Guid.forBinaryChecksum(copier) + ", templateGuid="
				+ result.guid;
		return result;
	}
	
	/** @param properties
	 * @return */
	@ReflectionExplicit
	RepairChecker createCheckContext(final BaseObject properties) {
		
		return this.driver.createCheckContext(properties);
	}
	
	@Override
	public RecImpl createContainerTemplate() {
		
		return this.driver.createContainerTemplate();
	}
	
	@Override
	public RecImpl createKeyForString(final String key) {
		
		final Guid inline = Guid.forString(key);
		if (inline != null) {
			return new RecInline(inline);
		}
		/** historically key is binary UTF-8, take care */
		final TransferCopier bytes = new WrapCopier(key.getBytes(StandardCharsets.UTF_8));
		final RecImpl result = this.driver.createBinaryTemplate(bytes);
		assert Guid.forBinaryChecksum(bytes).equals(result.guid) : "binary template should contain correct GUID, key=" + key;
		return result;
	}
	
	@Override
	public RecImpl createPrimitiveTemplate(final Guid guid) {
		
		assert guid != null : "createPrimitiveTemplate: guid is null";
		assert guid.isPrimitive() && guid.isInline() : "createPrimitiveTemplate: guid is not inline or primitive, guid: " + guid;
		return this.driver.createPrimitiveTemplate(guid);
	}
	
	@Override
	public RefImpl<RecImpl> createReferenceTemplate(final RecImpl key, final TreeLinkType mode, final RefImpl<RecImpl> original) {
		
		final RecImpl source = original.getSource();
		final RecImpl target = original.getTarget();
		final RefInline result = new RefInline();
		result.driver = this.driver;
		result.collection = source;
		result.key = key.guid;
		result.keyRecord = key;
		result.mode = mode;
		result.modified = original.modified;
		result.value = target.guid;
		result.valueRecord = target;
		return result;
	}
	
	@Override
	public RecImpl createTextTemplate(final CharSequence text) {
		
		final Guid inline = Guid.forString(text);
		if (inline != null) {
			return new RecInline(inline);
		}
		final TransferCopier copier = Transfer.createCopierUtf8(text);
		final RecImpl result = this.driver.createTextTemplate(copier);
		assert Guid.forBinaryText(copier).equals(result.guid) : "binary template should contain correct GUID, copierGuid=" + Guid.forBinaryText(copier) + ", templateGuid="
				+ result.guid;
		return result;
	}
	
	/** FIXME: tada! */
	@Override
	public ArsTransactionS4 createTransaction() throws Exception {
		
		return this.driver.createUnderlyingTransaction();
	}
	
	@Override
	public Value<RefImpl<RecImpl>> doLinkDelete(final RefImpl<RecImpl> template, final RecImpl object, final RecImpl key, final TreeLinkType mode) {
		
		return this.driver.doLinkSet(template, object, key, mode, -1L, null);
	}
	
	@Override
	public Value<RefImpl<RecImpl>> doLinkMoveRename(final RefImpl<RecImpl> template,
			final RecImpl object,
			final RecImpl key,
			final RecImpl newObject,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl target) {
		
		return this.driver.doLinkMoveRename(template, object, key, newObject, newKey, mode, modified, target);
	}
	
	@Override
	public Value<RefImpl<RecImpl>> doLinkRename(final RefImpl<RecImpl> template,
			final RecImpl object,
			final RecImpl key,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl target) {
		
		return this.driver.doLinkRename(template, object, key, newKey, mode, modified, target);
	}
	
	@Override
	public Value<RefImpl<RecImpl>>
			doLinkSet(final RefImpl<RecImpl> template, final RecImpl object, final RecImpl key, final TreeLinkType mode, final long modified, final RecImpl target) {
		
		return this.driver.doLinkSet(template, object, key, mode, modified, target);
	}
	
	@Override
	public Value<TransferCopier> getBinary(final RecImpl object) {
		
		assert object != null : "NULL object";
		final Guid guid = object.guid;
		assert guid != null : "NULL guid, objectClass=" + object.getClass().getName();
		if (guid.isInline()) {
			if (guid.isBinary()) {
				return (TransferCopier) guid.getInlineValue();
			}
			/** TODO: return some form of guid representation anyway (like 'cat' from console
			 * VFS) */
			return TransferCopier.NUL_COPIER;
		}
		final long length = guid.getBinaryLength();
		if (length > 0 && length <= S4Driver.TAIL_CAPACITY) {
			return this.driver.getRecordTail(object);
		}
		if (length == 0) {
			return TransferCopier.NUL_COPIER;
		}
		/** FIXME: TODO: this is important! */
		throw new UnsupportedOperationException("NOT WORKING YET - please call myx@meloscope.com\r\nguid: " + guid + "\r\ntype: " + guid.getType());
	}
	
	/** @return */
	@ReflectionExplicit
	@ReflectionEnumerable
	public String getClassName() {
		
		return this.getClass().getSimpleName();
	}
	
	/** @return */
	@ReflectionExplicit
	@ReflectionEnumerable
	public S4Driver<?, ?, ?, ?> getDriver() {
		
		return this.driver;
	}
	
	/** @return */
	@ReflectionExplicit
	@ReflectionEnumerable
	public String getKey() {
		
		return this.key;
	}
	
	@Override
	public Value<RefImpl<RecImpl>> getLink(final RecImpl object, final RecImpl key, final TreeLinkType mode) {
		
		return this.driver.getLink(object, key, mode);
	}
	
	@Override
	public Value<ArrImpl<RefImpl<RecImpl>>> getLinks(final RecImpl object, final TreeReadType mode) {
		
		assert object != null : "Record shouldn't be NULL";
		if (object.getClass() == RecInline.class || !object.isContainer()) {
			/** This was here before. Still unknown is it OK to return NULL but the only user of
			 * this method has it handled. */
			// assert object.getClass() != RecInline.class :
			// "GetLinks - INLINE source!";
			// assert object.isContainer() :
			// "GetLinks - not a container source!";
			return null;
		}
		return this.driver.getLinks(object, mode);
	}
	
	@Override
	public Value<ArrImpl<RefImpl<RecImpl>>>
			getLinksRange(final RecImpl object, final RecImpl keyStart, final RecImpl keyStop, final int limit, final boolean backwards, final TreeReadType mode) {
		
		assert object != null : "Record shouldn't be NULL";
		if (object.getClass() == RecInline.class || !object.isContainer()) {
			/** This was here before. Still unknown is it OK to return NULL but the only user of
			 * this method has it handled. */
			// assert object.getClass() != RecInline.class :
			// "GetLinks - INLINE source!";
			// assert object.isContainer() :
			// "GetLinks - not a container source!";
			return null;
		}
		return this.driver.getLinksRange(object, keyStart, keyStop, limit, backwards, mode);
	}
	
	@Override
	public RefImpl<RecImpl> getRootReference() {
		
		return this.driver.getRootReference();
	}
	
	@Override
	public Value<? extends CharSequence> getText(final RecImpl object) {
		
		assert object != null : "NULL object";
		final Guid guid = object.guid;
		assert guid != null : "NULL guid, objectClass=" + object.getClass().getName();
		if (guid.isInline()) {
			if (guid.isInlineString()) {
				return guid.getInlineBaseValue().baseToString();
			}
			if (guid.isBinaryText()) {
				return Base.forString((CharSequence) ((TransferCopier) guid.getInlineValue()).toStringUtf8());
			}
			/** TODO: return some form of guid representation anyway (like 'cat' from console
			 * VFS) */
			return BaseString.EMPTY;
		}
		final long length = guid.getBinaryLength();
		if (length > 0 && length <= S4Driver.TAIL_CAPACITY) {
			// TODO: return promise
			final TransferCopier baseValue = this.driver.getRecordTail(object).baseValue();
			if (baseValue == null) {
				return Base.forNull();
			}
			return Base.forString((CharSequence) baseValue.toStringUtf8());
		}
		if (length == 0) {
			return BaseString.EMPTY;
		}
		/** FIXME: TODO: this is important! */
		throw new UnsupportedOperationException("NOT WORKING YET - please call myx@meloscope.com\r\nguid: " + guid + "\r\ntype: " + guid.getType());
	}
	
	@Override
	public boolean isHistorySupported() {
		
		return false;
	}
	
	@Override
	public boolean isReadOnly() {
		
		return false;
	}
	
	@Override
	public void shutdown() throws Exception {
		
		if (!this.started) {
			return;
		}
		// only one try
		this.started = false;
		if (this.key != null) {
			StorageImplS4.INSTANCES.remove(this.key);
			this.key = null;
		}
		this.driver.stop();
	}
	
	/** @throws Exception */
	void start() throws Exception {
		
		if (this.started) {
			new Error(this.getClass().getName() + " is already started!").printStackTrace();
			return;
		}
		// only one try
		this.started = true;
		this.driver.start();
		this.key = this.driver.getKey();
		if (this.key != null) {
			StorageImplS4.INSTANCES.put(this.key, this);
		}
	}
	
	@Override
	public String toString() {
		
		return "[object " + this.getClass().getSimpleName() + "(" + this.driver + ")]";
	}
}
