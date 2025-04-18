/**
 *
 */
package ru.myx.ae3.vfs.s4.common;

import ru.myx.ae3.know.Guid;

/** @author myx */
public final class RecInline //
		extends
			RecImpl {

	/** @param guid */
	public RecInline(final Guid guid) {

		this.guid = guid;
	}

	@Override
	public String toString() {

		return "INLINE: " + this.guid;
	}
}
