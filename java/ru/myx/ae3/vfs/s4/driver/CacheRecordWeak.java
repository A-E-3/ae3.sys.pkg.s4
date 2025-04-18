package ru.myx.ae3.vfs.s4.driver;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import ru.myx.ae3.common.Value;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.s4.common.RecImpl;

final class CacheRecordWeak //
		extends
			WeakReference<Value<RecImpl>>
		implements
			CacheRecord {

	private Guid guid;

	CacheRecordWeak(//
			final Guid guid,
			final Value<RecImpl> record,
			final ReferenceQueue<Value<RecImpl>> queue) {

		super(record, queue);
		this.guid = guid;
	}

	@Override
	public void clear() {

		this.guid = null;
		super.clear();
	}

	@Override
	public Guid getGuid() {

		return this.guid;
	}

	@Override
	public String toString() {

		return this.getClass().getSimpleName() + "[" + this.isEnqueued() + "," + this.guid + "]";
	}
}
