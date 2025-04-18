package ru.myx.ae3.vfs.s4.lcl;

import java.util.TreeMap;

import ru.myx.ae3.vfs.s4.common.ArsTransactionS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;

class S4LocalXctNested //
		extends
			ArsTransactionS4 {

	private final ArsTransactionS4 parent;

	private final TreeMap<String, RecImpl> records = new TreeMap<>();

	private final TreeMap<String, RefImpl<RecImpl>> references = new TreeMap<>();

	S4LocalXctNested(final ArsTransactionS4 parent) {

		this.parent = parent;
	}

	@Override
	public void cancel() throws Exception {

		//
	}

	@Override
	public void commit() throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	public ArsTransactionS4 createTransaction() throws Exception {

		return new S4LocalXctNested(this);
	}
}
