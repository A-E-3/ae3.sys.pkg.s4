package ru.myx.ae3.vfs.s4.common;

import java.util.ArrayList;

import ru.myx.ae3.vfs.ars.ArsRefArray;

/** @author myx
 * @param <R> */
public class ArrRefImpl<R extends RefImpl<? extends RecImpl>> //
		extends
			ArrayList<R>
		implements
			ArsRefArray<R> {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -5033077639421840429L;
}
