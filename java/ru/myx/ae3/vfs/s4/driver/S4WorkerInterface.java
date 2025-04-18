package ru.myx.ae3.vfs.s4.driver;

import java.util.Collection;
import java.util.function.Function;

import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;

/** @author myx
 * @param <O>
 * @param <R>
 * @param <L> */
public interface S4WorkerInterface<O extends RecImpl, R extends RefImpl<O>, L extends Object> {

	/** Deletes a reference (unlinks/drops link)
	 *
	 * @param record
	 * @param key
	 * @param mode
	 * @param modified
	 * @throws Exception */
	void arsLinkDelete(//
			O record,
			Guid key,
			TreeLinkType mode,
			long modified) throws Exception;

	/** Updates existing reference, including move and rename. Modified date always updated to given
	 * value, except -1 make it current date.
	 *
	 * @param record
	 * @param newRecord
	 * @param key
	 * @param newKey
	 * @param mode
	 * @param modified
	 * @param value
	 * @throws Exception */
	void arsLinkUpdate(//
			O record,
			O newRecord,
			Guid key,
			Guid newKey,
			TreeLinkType mode,
			long modified,
			Guid value) throws Exception;

	/** Upserts new or existing record. Modified date of -1 will set currenrt date if there are any
	 * other changes to a record (not really a good idea for different implementations).
	 *
	 * @param record
	 * @param key
	 * @param mode
	 * @param modified
	 * @param value
	 * @throws Exception */
	void arsLinkUpsert(//
			O record,
			Guid key,
			TreeLinkType mode,
			long modified,
			Guid value) throws Exception;

	/** this method is ONLY called by storage garbage collection mechanism.
	 *
	 * @param record
	 * @throws Exception */
	void arsRecordDelete(//
			O record) throws Exception;

	/** this method is ONLY called by storage garbage collection mechanism.
	 *
	 * @param records
	 * @throws Exception */
	default void arsRecordsDelete(final Collection<O> records) throws Exception {

		for (final O record : records) {
			this.arsRecordDelete(record);
		}
	}

	/** this method is ONLY called by storage garbage collection mechanism.
	 *
	 * @param records
	 * @throws Exception */
	default void arsRecordsUpsert(final Collection<O> records) throws Exception {

		for (final O record : records) {
			this.arsRecordUpsert(record);
		}
	}

	/** @param record
	 * @throws Exception */
	void arsRecordUpsert(//
			O record) throws Exception;

	/** Should initialize fields: 'key', 'mode', 'modified', 'value'
	 *
	 * @param target
	 * @param record
	 * @param keyStart
	 *            inclusive key to start iteration with
	 * @param keyStop
	 *            exclusive key to stop iteration before
	 * @param limit
	 *            zero or the limit the the number of rows to be returned
	 * @param backwards
	 *            when backwards is 'true' the keyStart is supposed to be greater than keyStop
	 * @return number of items read
	 * @throws Exception */
	int readContainerContentsRange(//
			Function<R, ?> target,
			O record,
			Guid keyStart,
			Guid keyStop,
			int limit,
			boolean backwards) throws Exception;

	/** Should initialize fields: 'key', 'mode', 'modified', 'value'
	 *
	 * @param record
	 * @param key
	 * @return
	 * @throws Exception */
	R readContainerElement(//
			O record,
			Guid key) throws Exception;

	/** @param guid
	 * @return
	 * @throws Exception */
	O readRecord(//
			Guid guid) throws Exception;

	/** For tails - GUID type CRC384_XXXXXX for TAIL_CAPACITY (1500) bytes length binaries only!
	 *
	 * @param record
	 * @return
	 * @throws Exception */
	byte[] readRecordTail(//
			O record) throws Exception;

	/** @param target
	 * @param key
	 * @param value1
	 * @param value2
	 * @param limit
	 *            zero or the limit the the number of rows to be returned
	 * @return count of records found (explicitly, runs synchonously)
	 * @throws Exception */
	int searchBetween(//
			Function<L, ?> target,
			Guid key,
			Guid value1,
			Guid value2,
			int limit) throws Exception;

	/** @param target
	 * @param key
	 * @param value
	 * @param limit
	 *            zero or the limit the the number of rows to be returned
	 * @return count of records found (explicitly, runs synchonously)
	 * @throws Exception */
	int searchEquals(//
			Collection<L> target,
			Guid key,
			Guid value,
			int limit) throws Exception;
}
