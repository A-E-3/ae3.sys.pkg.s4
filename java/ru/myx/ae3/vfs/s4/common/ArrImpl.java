package ru.myx.ae3.vfs.s4.common;

import java.util.ArrayList;

import ru.myx.ae3.vfs.ars.ArsArray;

/** @author myx
 * @param <R> */
public class ArrImpl<R extends RefImpl<? extends RecImpl>> //
		extends
			ArrayList<R>
		implements
			ArsArray<R> {

	/**
	 *
	 */
	private static final long serialVersionUID = -5033077639421840429L;
}
