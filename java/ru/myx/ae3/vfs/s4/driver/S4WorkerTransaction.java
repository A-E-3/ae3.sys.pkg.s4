package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.common.Transaction;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;

/** @author myx
 * @param <O>
 * @param <R>
 * @param <L> */
public interface S4WorkerTransaction<O extends RecImpl, R extends RefImpl<O>, L extends Object> //
		extends
			S4WorkerInterface<O, R, L>,
			Transaction {
	
	//
}
