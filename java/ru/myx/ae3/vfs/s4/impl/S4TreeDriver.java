package ru.myx.ae3.vfs.s4.impl;

import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.common.RepairChecker;
import ru.myx.ae3.vfs.s4.common.S4StoreType;

/** @author myx
 * @param <O>
 *            type of records
 * @param <R>
 *            type of references
 * @param <L>
 *            type of local identifiers */
public interface S4TreeDriver<O extends RecImpl, R extends RefImpl<O>, L extends Object> {
	
	/** @param properties
	 * @return */
	RepairChecker createCheckContext(final BaseObject properties);
	
	/** @return */
	O createRecordTemplate();
	
	/** @param key
	 * @param mode
	 * @param target
	 * @return */
	R createReferenceTemplate();
	
	/** @return */
	S4TreeWorker<O, R, L> createWorker();
	
	/** @return */
	String getKey();
	
	/** TODO: replace 'type' with 'props'
	 * 
	 * @param type
	 * @throws Exception */
	void setup(final S4StoreType type) throws Exception;
	
	/** @throws Exception */
	void start() throws Exception;
	
	/** @throws Exception */
	void stop() throws Exception;
	
	/** @return
	 * @throws Exception */
	long storageCalculate() throws Exception;
	
	/** @throws Exception */
	void storageTruncate() throws Exception;
}
