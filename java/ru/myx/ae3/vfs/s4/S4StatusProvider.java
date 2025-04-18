package ru.myx.ae3.vfs.s4;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.status.StatusInfo;
import ru.myx.ae3.status.StatusProvider;

/*
 * Created on 20.12.2005
 */
/** @author myx */
public final class S4StatusProvider //
		implements
			StatusProvider {

	static long stDoRenameAttempts = 0;

	static long stDoRenameSuccess = 0;

	static long stDoSetBinaryAttempts = 0;

	static long stDoSetBinarySuccess = 0;

	static long stDoSetContainerAttempts = 0;

	static long stDoSetContainerSuccess = 0;

	static long stDoSetHardlinkAttempts = 0;

	static long stDoSetModifiedAttempts = 0;

	static long stDoSetModifiedSuccess = 0;

	static long stDoSetPrimitiveAttempts = 0;

	static long stDoSetPrimitiveSuccess = 0;

	static long stDoSetTextAttempts = 0;

	static long stDoSetTextSuccess = 0;

	static long stDoUnlinkAttempts = 0;

	static long stDoUnlinkSuccess = 0;

	static long stGetBinaryContentLength = 0;

	static long stGetCharacterContentLength = 0;

	static long stGetLastModified = 0;

	static long stIsContainerEmpty = 0;

	static long stReadBinaryContent = 0;

	static long stReadContentCollection = 0;

	static long stReadContentElement = 0;

	static long stReadContentPrimitive = 0;

	static long stReadContentValue = 0;

	static long stReadTextContent = 0;

	/** @return */
	public static long getStatsReadBinaryContent() {

		return S4StatusProvider.stReadBinaryContent;
	}

	/** @return */
	public static long getStatsReadContentCollection() {

		return S4StatusProvider.stReadContentCollection;
	}

	/** @return */
	public static long getStatsReadContentElement() {

		return S4StatusProvider.stReadContentElement;
	}

	/** @return */
	public static long getStatsReadContentPrimitive() {

		return S4StatusProvider.stReadContentPrimitive;
	}

	/** @return */
	public static long getStatsReadContentValue() {

		return S4StatusProvider.stReadContentValue;
	}

	/** @return */
	public static long getStatsReadIsContainerEmpty() {

		return S4StatusProvider.stIsContainerEmpty;
	}

	/** @return */
	public static long getStatsReadTextContent() {

		return S4StatusProvider.stReadTextContent;
	}

	/** @return */
	public static long getStatsTotalReadOperations() {

		final long stGetBinaryContent = S4StatusProvider.stReadBinaryContent;
		final long stGetContentCollection = S4StatusProvider.stReadContentCollection;
		final long stGetContentElement = S4StatusProvider.stReadContentElement;
		final long stGetContentPrimitive = S4StatusProvider.stReadContentPrimitive;
		final long stGetContentValue = S4StatusProvider.stReadContentValue;
		final long stGetTextContent = S4StatusProvider.stReadTextContent;
		final long stIsContainerEmpty = S4StatusProvider.stIsContainerEmpty;
		return stGetBinaryContent + stGetContentCollection + stGetContentElement + stGetContentPrimitive + stGetContentValue + stGetTextContent + stIsContainerEmpty;
	}

	/** @return */
	public static long getStatsTotalWriteOperations() {

		final long stDoSetBinary = S4StatusProvider.stDoSetBinaryAttempts;
		final long stDoSetText = S4StatusProvider.stDoSetTextAttempts;
		final long stDoSetContainer = S4StatusProvider.stDoSetContainerAttempts;
		final long stDoSetModified = S4StatusProvider.stDoSetModifiedAttempts;
		final long stDoSetPrimitive = S4StatusProvider.stDoSetPrimitiveAttempts;
		final long stDoUnlink = S4StatusProvider.stDoUnlinkAttempts;
		return stDoSetBinary + stDoSetText + stDoSetContainer + stDoSetModified + stDoSetPrimitive + stDoUnlink;
	}

	/** @return */
	public static long getStatsWriteDoUnlink() {

		return S4StatusProvider.stDoUnlinkAttempts;
	}

	/** @return */
	public static long getStatsWriteSetBinary() {

		return S4StatusProvider.stDoSetBinaryAttempts;
	}

	/** @return */
	public static long getStatsWriteSetContainer() {

		return S4StatusProvider.stDoSetContainerAttempts;
	}

	/** @return */
	public static long getStatsWriteSetHardlink() {

		return S4StatusProvider.stDoSetHardlinkAttempts;
	}

	/** @return */
	public static long getStatsWriteSetModified() {

		return S4StatusProvider.stDoSetModifiedAttempts;
	}

	/** @return */
	public static long getStatsWriteSetPrimitive() {

		return S4StatusProvider.stDoSetPrimitiveAttempts;
	}

	/** @return */
	public static long getStatsWriteSetText() {

		return S4StatusProvider.stDoSetTextAttempts;
	}

	private static final boolean statusFillAssert(final StatusInfo data) {

		data.put(
				"Access, getBinaryContentLength", //
				Format.Compact.toDecimal(S4StatusProvider.stGetBinaryContentLength));
		data.put(
				"Access, getCharacterContentLength", //
				Format.Compact.toDecimal(S4StatusProvider.stGetCharacterContentLength));
		data.put(
				"Access, getLastModified", //
				Format.Compact.toDecimal(S4StatusProvider.stGetLastModified));
		return true;
	}

	@Override
	public String statusDescription() {

		return "VFS";
	}

	@Override
	public void statusFill(final StatusInfo data) {

		final long stDoSetBinary = S4StatusProvider.stDoSetBinaryAttempts;
		final long stDoSetBinarySuccess = S4StatusProvider.stDoSetBinarySuccess;
		final long stDoSetText = S4StatusProvider.stDoSetTextAttempts;
		final long stDoSetTextSuccess = S4StatusProvider.stDoSetTextSuccess;
		final long stDoSetContainer = S4StatusProvider.stDoSetContainerAttempts;
		final long stDoSetContainerSuccess = S4StatusProvider.stDoSetContainerSuccess;
		final long stDoSetModified = S4StatusProvider.stDoSetModifiedAttempts;
		final long stDoSetModifiedSuccess = S4StatusProvider.stDoSetModifiedSuccess;
		final long stDoSetPrimitive = S4StatusProvider.stDoSetPrimitiveAttempts;
		final long stDoSetPrimitiveSuccess = S4StatusProvider.stDoSetPrimitiveSuccess;
		final long stDoUnlink = S4StatusProvider.stDoUnlinkAttempts;
		final long stDoUnlinkSuccess = S4StatusProvider.stDoUnlinkSuccess;

		data.put("Write, doSetBinary attempts", Format.Compact.toDecimal(stDoSetBinary));
		data.put("Write, doSetBinary success", Format.Compact.toDecimal(stDoSetBinarySuccess));
		data.put("Write, doSetText attempts", Format.Compact.toDecimal(stDoSetText));
		data.put("Write, doSetText success", Format.Compact.toDecimal(stDoSetTextSuccess));
		data.put("Write, doSetContainer attempts", Format.Compact.toDecimal(stDoSetContainer));
		data.put("Write, doSetContainer success", Format.Compact.toDecimal(stDoSetContainerSuccess));
		data.put("Write, doSetModified attempts", Format.Compact.toDecimal(stDoSetModified));
		data.put("Write, doSetModified success", Format.Compact.toDecimal(stDoSetModifiedSuccess));
		data.put("Write, doSetPrimitive attempts", Format.Compact.toDecimal(stDoSetPrimitive));
		data.put("Write, doSetPrimitive success", Format.Compact.toDecimal(stDoSetPrimitiveSuccess));
		data.put("Write, doUnlink attempts", Format.Compact.toDecimal(stDoUnlink));
		data.put("Write, doUnlink success", Format.Compact.toDecimal(stDoUnlinkSuccess));

		final long stGetBinaryContent = S4StatusProvider.stReadBinaryContent;
		final long stGetContentCollection = S4StatusProvider.stReadContentCollection;
		final long stGetContentElement = S4StatusProvider.stReadContentElement;
		final long stGetContentPrimitive = S4StatusProvider.stReadContentPrimitive;
		final long stGetContentValue = S4StatusProvider.stReadContentValue;
		final long stGetTextContent = S4StatusProvider.stReadTextContent;
		final long stIsContainerEmpty = S4StatusProvider.stIsContainerEmpty;

		data.put("Read, getBinaryContent", Format.Compact.toDecimal(stGetBinaryContent));
		data.put("Read, getContentCollection", Format.Compact.toDecimal(stGetContentCollection));
		data.put("Read, getContentElement", Format.Compact.toDecimal(stGetContentElement));
		data.put("Read, getContentPrimitive", Format.Compact.toDecimal(stGetContentPrimitive));
		data.put("Read, getContentValue", Format.Compact.toDecimal(stGetContentValue));
		data.put("Read, getTextContent", Format.Compact.toDecimal(stGetTextContent));
		data.put("Read, isContainerEmpty", Format.Compact.toDecimal(stIsContainerEmpty));

		assert S4StatusProvider.statusFillAssert(data);
	}

	@Override
	public String statusName() {

		return "s4";
	}
}
