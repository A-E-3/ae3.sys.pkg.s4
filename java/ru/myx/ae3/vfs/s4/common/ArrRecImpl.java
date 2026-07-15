package ru.myx.ae3.vfs.s4.common;

import java.util.ArrayList;

import ru.myx.ae3.vfs.ars.ArsRecArray;

/** @author myx
 * @param <O> */
public class ArrRecImpl<O extends RecImpl> //
		extends
			ArrayList<O>
		implements
			ArsRecArray<O> {

	/**
	 *
	 */
	private static final long serialVersionUID = -5033077639421840429L;
}
