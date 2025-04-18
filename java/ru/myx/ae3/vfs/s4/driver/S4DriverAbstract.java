package ru.myx.ae3.vfs.s4.driver;

import java.lang.ref.ReferenceQueue;
import java.util.Map;

import ru.myx.ae3.Engine;
import ru.myx.ae3.act.ServiceThread;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.ArrImpl;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RecTemplate;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.common.RepairChecker;
import ru.myx.ae3.vfs.s4.common.S4StoreType;
import ru.myx.ae3.vfs.s4.impl.S4TreeDriver;

/** @author myx */
public abstract class S4DriverAbstract //
		implements
			S4Driver<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>, Object> {

	final static CacheRecord.Mode CACHE_RECORD_MODE = CacheRecord.Mode.getDefaultMode();

	/** WARNING, casted to this type - use only instances provided by implementation. */
	protected final S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> driver;

	/** cache */
	protected final Map<Guid, CacheRecord> recordsByGuid;

	final CacheMaintainer recordsMaintainer;

	final ReferenceQueue<Value<RecImpl>> recordsQueue;

	/** Root record for this instance. It is used as a host for instance settings (such as ticks),
	 * transaction root and root for real data. */
	protected RecImpl rootRecord;

	/** Root reference, VFS uses references to build a file-system view. */
	protected Value<RefImpl<RecImpl>> rootReference;

	/** Monotonous time. Every tick is approximately 1 minute. After start, every minute system
	 * increments a tick, saves it's value and performs this tick's maintenance. Then next loop is
	 * started. */
	protected int scheduleTicks;

	/**
	 *
	 */
	public int stsTasksErrors = 0;

	/**
	 *
	 */
	public int stsTasksFinished = 0;

	/**
	 *
	 */
	public int stsTasksStarted = 0;

	/**
	 *
	 */
	protected final S4StoreType type;

	/** @param driver
	 * @param type */
	protected S4DriverAbstract(final S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> driver, final S4StoreType type) {

		this.driver = driver;
		this.type = type;
		this.recordsByGuid = S4DriverAbstract.CACHE_RECORD_MODE.createCache();
		this.recordsQueue = new ReferenceQueue<>();
		this.recordsMaintainer = new CacheMaintainer(this, this.recordsQueue);
	}

	@Override
	public final RecImpl createBinaryTemplate(final TransferCopier copier) {

		final RecImpl result = this.driver.createRecordTemplate();
		result.guid = Guid.forBinaryChecksum(copier);
		assert (result.runtimeState & RecImpl.RT_TEMPLATE) == 0 : "Use of system-reserved bits!";
		if (result.guid.isInline()) {
			result.runtimeState &= ~RecImpl.RT_TEMPLATE;
		} else {
			result.runtimeState |= RecImpl.RT_TEMPLATE;
			((RecTemplate) result).setAttachment(copier);
		}
		return result;
	}
	
	@Override
	public RepairChecker createCheckContext(final BaseObject properties) {

		return this.driver.createCheckContext(properties);
	}

	@Override
	public final RecImpl createContainerTemplate() {

		final RecImpl result = this.driver.createRecordTemplate();
		result.guid = Guid.createGuid184();
		assert (result.runtimeState & RecImpl.RT_TEMPLATE) == 0 : "Use of system-reserved bits!";
		result.runtimeState |= RecImpl.RT_TEMPLATE;
		return result;
	}

	@Override
	public final RecImpl createPrimitiveTemplate(final Guid guid) {

		final RecImpl result = this.driver.createRecordTemplate();
		result.guid = guid;
		assert (result.runtimeState & RecImpl.RT_TEMPLATE) == 0 : "Use of system-reserved bits!";
		result.runtimeState &= ~RecImpl.RT_TEMPLATE;
		return result;
	}

	short createScheduleFresh() {

		final int realTicks = this.scheduleTicks & 0x7FFFFFFF;
		final int scheduleTicks = realTicks / 6;
		final double delay = this.type.scheduleTicksFresh();
		final S4ScheduleType slot = S4ScheduleType.randomFreshSlot();
		final int limit = slot.scheduleTicks();
		if (delay <= limit) {
			/** next check: (100 - 10)% to (100 + 25)% of requested delay. */
			final short randomized = (short) Math.min(limit, delay + delay * (Engine.createRandom(14000) - 4000) / 40000);
			return (short) (slot.baseOffset() + (scheduleTicks + randomized) % limit);
		}
		throw new IllegalArgumentException("Fresh schedule delay: " + delay + " minutes is not supported!");
	}

	/** @param currentValue
	 *            record's current schedule value
	 * @param rewriteProbability
	 *            0.0 to 1.0, the probability that schedule will be rewritten when there is no need
	 *            to.
	 * @return */
	short createScheduleReferenced(final short currentValue, final double rewriteProbability) {

		final int realTicks = this.scheduleTicks & 0x7FFFFFFF;
		final int scheduleTicks = realTicks / 6;

		/** already in range of SEVEN_DAY_REFERENCED schedule? */
		if (currentValue >= S4ScheduleType.REFERENCED_START_INCLUDING && currentValue < S4ScheduleType.REFERENCED_STOP_EXCLUDING) {
			/** higher probability of keeping the same value, less writes */
			if (rewriteProbability < 1.0 && Engine.createRandomDouble() >= rewriteProbability) {
				return currentValue;
			}
		}

		{
			final double delay = 7 * 24 * 60;
			final S4ScheduleType slot = S4ScheduleType.randomReferencedSlot();
			final short limit = slot.scheduleTicks();
			/** next check: 75% to 100% of full loop. */
			final short randomized = (short) Math.min(limit, delay + delay * (Engine.createRandom(10000) - 10000) / 40000);
			return (short) (slot.baseOffset() + (scheduleTicks + randomized) % limit);
		}
	}

	@Override
	public final RecImpl createTextTemplate(final TransferCopier copier) {

		final RecImpl result = this.driver.createRecordTemplate();
		result.guid = Guid.forBinaryText(copier);
		assert (result.runtimeState & RecImpl.RT_TEMPLATE) == 0 : "Use of system-reserved bits!";
		if (result.guid.isInline()) {
			result.runtimeState &= ~RecImpl.RT_TEMPLATE;
		} else {
			result.runtimeState |= RecImpl.RT_TEMPLATE;
			((RecTemplate) result).setAttachment(copier);
		}
		return result;
	}

	@Override
	public final Value<RefImpl<RecImpl>> doLinkMoveRename(final RefImpl<RecImpl> template,
			final RecImpl record,
			final RecImpl key,
			final RecImpl newRecord,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl value) {

		if (record == newRecord) {
			return this.doLinkRename(template, newRecord, key, newKey, mode, modified, value);
		}
		
		final RefImpl<RecImpl> templateToUse;
		if (template == null) {
			templateToUse = this.driver.createReferenceTemplate();
			templateToUse.mode = mode;
		} else {
			templateToUse = template;
		}
		final TaskCommon<RefImpl<RecImpl>> task = new TaskLocalLinkMoveRename(
				templateToUse, //
				record,
				key,
				newRecord,
				newKey,
				mode,
				modified,
				value);
		this.enqueue(task);
		return task;
	}

	@Override
	public final Value<RefImpl<RecImpl>> doLinkRename(final RefImpl<RecImpl> template,
			final RecImpl record,
			final RecImpl key,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl value) {

		final RefImpl<RecImpl> templateToUse;
		if (template == null) {
			templateToUse = this.driver.createReferenceTemplate();
			templateToUse.mode = mode;
		} else {
			templateToUse = template;
		}
		final TaskCommon<RefImpl<RecImpl>> task = new TaskLocalLinkRename(
				templateToUse, //
				record,
				key,
				newKey,
				mode,
				modified,
				value);
		this.enqueue(task);
		return task;
	}

	@Override
	public final Value<RefImpl<RecImpl>>
			doLinkSet(final RefImpl<RecImpl> template, final RecImpl record, final RecImpl key, final TreeLinkType mode, final long modified, final RecImpl value) {

		final RefImpl<RecImpl> templateToUse;
		if (template == null) {
			templateToUse = this.driver.createReferenceTemplate();
			templateToUse.mode = mode;
		} else {
			templateToUse = template;
		}
		final TaskCommon<RefImpl<RecImpl>> task = new TaskLocalLinkSet(
				templateToUse, //
				record,
				key,
				mode,
				modified,
				value);
		this.enqueue(task);
		return task;
	}

	/** public, a generic task
	 *
	 * @param task */
	protected abstract void enqueue(TaskCommon<?> task);

	/** @throws Exception */
	protected void executeDriverShutdown() throws Exception {

		//
	}

	/** @throws Exception */
	protected void executeDriverStart() throws Exception {

		new ServiceThread<>(null, this.recordsMaintainer).start();
	}

	/** @throws Exception */
	protected void executeDriverTerminate() throws Exception {

		/** done in 'stop' */
		// this.recordsMaintainer.stop();
		// this.records.clear();
	}

	@Override
	public final Value<RefImpl<RecImpl>> getLink(final RecImpl record, final RecImpl key, final TreeLinkType mode) {

		final TaskCommon<RefImpl<RecImpl>> task = new TaskLocalGetLink(record, key, mode);
		this.enqueue(task);
		return task;
	}

	@Override
	public final Value<ArrImpl<RefImpl<RecImpl>>> getLinks(final RecImpl record, final TreeReadType mode) {

		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "GetLinks - INLINE source!";
		assert record.isContainer() : "GetLinks - not a container source!";
		final TaskCommon<ArrImpl<RefImpl<RecImpl>>> task = new TaskLocalGetLinks(this, record, mode);
		this.enqueue(task);
		return task;
	}

	@Override
	public final Value<ArrImpl<RefImpl<RecImpl>>>
			getLinksRange(final RecImpl record, final RecImpl keyStart, final RecImpl keyStop, final int limit, final boolean backwards, final TreeReadType mode) {

		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "GetLinks - INLINE source!";
		assert record.isContainer() : "GetLinks - not a container source!";
		final TaskCommon<ArrImpl<RefImpl<RecImpl>>> task = new TaskLocalGetLinksRange(this, record, keyStart, keyStop, limit, backwards, mode);
		this.enqueue(task);
		return task;
	}

	@Override
	public final Value<RecImpl> getRecordByGuid(final Guid guid) {

		if (guid == null || !guid.isValid()) {
			return null;
		}
		if (guid.isInline()) {
			return new RecInline(guid);
		}
		final CacheRecord reference = this.recordsByGuid.get(guid);
		if (reference != null) {
			final Value<RecImpl> record = reference.get();
			if (record != null) {
				return record;
			}
		}
		{
			final TaskCommon<RecImpl> task = new TaskLocalGetRecord(guid);
			this.enqueue(task);
			return task;
		}
	}

	@Override
	public final Value<TransferCopier> getRecordTail(final RecImpl record) {

		assert record != null : "Record shouldn't be NULL";
		assert record.getClass() != RecInline.class : "GetTail - INLINE source!";
		assert record.isBinary() : "GetTail - not a binary record!";
		if (record instanceof final RecTemplate recTemplate) {
			return (TransferCopier) recTemplate.getAttachment();
		}
		final TaskCommon<TransferCopier> task = new TaskLocalGetTail(record);
		this.enqueue(task);
		return task;
	}

	@Override
	public final RefImpl<RecImpl> getRootReference() {

		return this.rootReference.baseValue();
	}

	/** @return */
	public S4StoreType getStoreType() {

		return this.type;
	}

	@Override
	public boolean start() throws Exception {

		this.driver.setup(this.type);
		if (this.type == S4StoreType.TEST) {
			this.driver.storageTruncate();
			StorageImplS4.log(EventLevel.NOTICE, "S4DRV:START", "START", "Test storage truncated on start");
		}
		return true;
	}

	@Override
	public boolean stop() throws Exception {

		this.recordsMaintainer.stop();
		this.recordsByGuid.clear();
		return true;
	}

	@Override
	public String toString() {

		return "[object " + this.getClass().getSimpleName() + "(" + this.driver + ")]";
	}
}
