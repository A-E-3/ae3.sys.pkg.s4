package ru.myx.ae3.vfs.s4.common;

import java.util.Map;

import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.ae3.vfs.ars.ArsTransaction;

/** @author myx */
public abstract class ArsTransactionS4 //
		implements
			ArsTransaction<RecImpl, RefImpl<RecImpl>, ArrImpl<RefImpl<RecImpl>>> {

	Map<Guid, Object> deleteItems = null;

	Map<Guid, Object> deleteTree = null;

	Map<Guid, Object> updateItems = null;

	Map<Guid, Object> updateTree = null;

	@Override
	public abstract void cancel() throws Exception;

	@Override
	public abstract void commit() throws Exception;

	@Override
	public RecImpl createBinaryTemplate(final TransferCopier copier) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RecImpl createContainerTemplate() {

		throw new UnsupportedOperationException();
	}

	@Override
	public RecImpl createKeyForString(final String key) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RecImpl createPrimitiveTemplate(final Guid guid) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RefImpl<RecImpl> createReferenceTemplate(final RecImpl key, final TreeLinkType mode, final RefImpl<RecImpl> original) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RecImpl createTextTemplate(final CharSequence text) {

		throw new UnsupportedOperationException();
	}

	@Override
	public abstract ArsTransactionS4 createTransaction() throws Exception;

	@Override
	public Value<RefImpl<RecImpl>> doLinkDelete(final RefImpl<RecImpl> template, final RecImpl object, final RecImpl key, final TreeLinkType mode) {

		throw new UnsupportedOperationException();
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

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<RefImpl<RecImpl>> doLinkRename(final RefImpl<RecImpl> template,
			final RecImpl object,
			final RecImpl key,
			final RecImpl newKey,
			final TreeLinkType mode,
			final long modified,
			final RecImpl target) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<RefImpl<RecImpl>>
			doLinkSet(final RefImpl<RecImpl> template, final RecImpl object, final RecImpl key, final TreeLinkType mode, final long modified, final RecImpl target) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<TransferCopier> getBinary(final RecImpl object) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<RefImpl<RecImpl>> getLink(final RecImpl object, final RecImpl key, final TreeLinkType mode) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<ArrImpl<RefImpl<RecImpl>>> getLinks(final RecImpl object, final TreeReadType mode) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<ArrImpl<RefImpl<RecImpl>>>
			getLinksRange(final RecImpl object, final RecImpl keyStart, final RecImpl keyStop, final int limit, final boolean backwards, final TreeReadType mode) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Value<CharSequence> getText(final RecImpl object) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isHistorySupported() {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly() {

		throw new UnsupportedOperationException();
	}

}
