package ru.myx.ae3.vfs.s4.driver;

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

/** @author myx
 *
 * @param <O>
 * @param <R>
 * @param <A>
 * @param <L> */
public interface S4Driver<O extends RecImpl, R extends RefImpl<O>, A extends ArrImpl<R>, L extends Object> {

	/** This key defines a link from Root Record to a 'mounted' attribute, to check if this instance
	 * was dismounted successfully. */
	static Guid KEY_MOUNTED = Guid.forString("mounted");

	/** This key defines a link from Root Record to a last saved monotonous time value. */
	static Guid KEY_SCHEDULE_TICKS = Guid.forString("scheduleTicks");

	/** All inner DB implementations must support explicit tails of this size */
	int TAIL_CAPACITY = 2048;

	/** @param copier
	 * @return */
	O createBinaryTemplate(TransferCopier copier);

	/** @param properties
	 * @return */
	RepairChecker createCheckContext(final BaseObject properties);

	/** @return */
	O createContainerTemplate();

	/** @param guid
	 * @return */
	O createPrimitiveTemplate(Guid guid);

	/** @param copier
	 * @return */
	O createTextTemplate(TransferCopier copier);

	/** @return */
	ArsTransactionS4 createUnderlyingTransaction();

	/** @param template
	 * @param record
	 * @param key
	 * @param newRecord
	 * @param newKey
	 * @param mode
	 * @param modified
	 * @param value
	 * @return */
	Value<R> doLinkMoveRename(R template, O record, O key, O newRecord, O newKey, TreeLinkType mode, long modified, O value);

	/** @param template
	 * @param record
	 * @param key
	 * @param newKey
	 * @param mode
	 * @param modified
	 * @param value
	 * @return */
	Value<R> doLinkRename(R template, O record, O key, O newKey, TreeLinkType mode, long modified, O value);

	/** @param template
	 * @param record
	 * @param key
	 * @param mode
	 * @param modified
	 *            - has no meaning when value is null
	 * @param value
	 *            - null value does unlink (linkDelete) and modified has no meaning in that case.
	 * @return */
	Value<R> doLinkSet(R template, O record, O key, TreeLinkType mode, long modified, O value);

	/** @return */
	String getKey();

	/** @param record
	 * @param key
	 * @param mode
	 * @return */
	Value<R> getLink(O record, O key, TreeLinkType mode);

	/** TODO: eliminate and replace with 'getLinksRange' or better with smart
	 *
	 * @param record
	 * @param mode
	 * @return */
	Value<ArrImpl<RefImpl<RecImpl>>> getLinks(O record, TreeReadType mode);

	/** @param record
	 * @param keyStart
	 * @param keyStop
	 * @param limit
	 * @param backwards
	 * @param mode
	 * @return */
	Value<ArrImpl<RefImpl<RecImpl>>> getLinksRange(O record, O keyStart, O keyStop, int limit, boolean backwards, TreeReadType mode);

	/** Get Record implementation for GUID
	 *
	 * @param guid
	 * @return */
	Value<O> getRecordByGuid(Guid guid);

	/** For tails - GUID type CRC384_XXXXXX for 50..TAIL_CAPACITY (1500) bytes length binaries only!
	 *
	 * @param record
	 * @return */
	Value<TransferCopier> getRecordTail(O record);

	/** @return */
	RefImpl<RecImpl> getRootReference();

	/** @return true
	 * @throws Exception */
	boolean start() throws Exception;

	/** @return true
	 * @throws Exception */
	boolean stop() throws Exception;
}
