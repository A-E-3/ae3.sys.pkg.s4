package ru.myx.ae3.vfs.s4.common;

import ru.myx.ae3.common.Value;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.ars.ArsReference;
import ru.myx.ae3.vfs.s4.driver.S4Driver;

/** @author myx
 * @param <O> */
public abstract class RefImpl<O extends RecImpl> //
		implements
			Value<RefImpl<O>>,
			ArsReference<O> {

	/**
	 *
	 */
	public O collection;

	/**
	 *
	 */
	public S4Driver<O, ?, ?, ?> driver;

	/**
	 *
	 */
	public Guid key;

	/**
	 *
	 */
	public O keyRecord;

	/**
	 *
	 */
	public TreeLinkType mode;

	/**
	 *
	 */
	public long modified;

	/**
	 *
	 */
	public Guid value;

	/**
	 *
	 */
	public O valueRecord;

	@Override
	public final RefImpl<O> baseValue() {

		return this;
	}

	/** @return */
	@Override
	public final O getKey() {

		assert this.driver != null : "Driver is NULL for record";
		assert this.key != null && this.key.isValid() : "Key is not valid";
		return this.keyRecord != null
			? this.keyRecord
			: (this.keyRecord = this.driver.getRecordByGuid(this.key).baseValue());
	}

	@Override
	public final String getKeyString() {

		return String.valueOf(this.key.getInlineValue());
	}

	@Override
	public final long getLastModified() {

		return this.modified;
	}

	/** @return */
	@Override
	public final TreeLinkType getLinkageMode() {

		return this.mode;
	}

	@Override
	public final O getSource() {

		return this.collection;
	}

	/** @return */
	@Override
	public O getTarget() {

		assert this.driver != null : "Driver is NULL for record";
		if (this.value == null) {
			return null;
		}
		assert this.value.isValid() : "Value is not valid, value=" + this.value + ", class=" + this.value.getClass().getName();
		return this.valueRecord != null
			? this.valueRecord
			: (this.valueRecord = this.driver.getRecordByGuid(this.value).baseValue());
	}

	/** Synonym to 'this.value != null' */
	@Override
	public boolean isExist() {

		return this.value != null;
	}

	@Override
	public final String toString() {

		return this.collection.guid + "[" + this.key + "] = " + this.value;
	}
}
