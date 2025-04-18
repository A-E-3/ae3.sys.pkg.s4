package ru.myx.ae3.vfs.s4.driver;

import java.io.Closeable;
import java.util.function.Function;

import ru.myx.ae3.vfs.ars.ArsRecord;
import ru.myx.ae3.vfs.ars.ArsReference;

/** @author myx */
public interface RepairCheckScanner //
		extends
			Closeable {

	/** @param limit
	 * @param target
	 * @return */
	boolean scanMoreRecords(final int limit, final Function<ArsRecord, ?> target);

	/** @param limit
	 * @param target
	 * @return */
	boolean scanMoreReferences(final int limit, final Function<ArsReference<?>, ?> target);
}
