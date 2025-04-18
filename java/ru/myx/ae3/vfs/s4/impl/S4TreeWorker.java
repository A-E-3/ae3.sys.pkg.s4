package ru.myx.ae3.vfs.s4.impl;

import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.driver.S4WorkerInterface;
import ru.myx.ae3.vfs.s4.driver.S4WorkerTransaction;
import ru.myx.util.BasicQueue;

/** @author myx
 * @param <O>
 *            type of records
 * @param <R>
 *            type of references
 * @param <L>
 *            type of local identifiers */
public interface S4TreeWorker<O extends RecImpl, R extends RefImpl<O>, L extends Object> {

	/** Commits atomic changes, upper level transactions have nothing to do with local transactions.
	 *
	 * This method called on every upper level global operation on some checkpoints, can be skipped,
	 * or implementation can commit using it's own timer thread.
	 *
	 * Choose between commitLowLevel and commitHighLevel to adjust performance.
	 *
	 * When implementation uses delayed or pooled transactions it should do commit on this method
	 * call, it called on shutdown and when manager wants to be sure that changes are not going to
	 * be lost.
	 *
	 * @throws Exception */
	void commitHighLevel() throws Exception;

	/** Commits atomic changes, upper level transactions have nothing to do with local transactions.
	 *
	 * This method called on every upper level atomic operation, can be skipped, or implementation
	 * can commit on it's own low-level operations.
	 *
	 * Choose between commitLowLevel and commitHighLevel to adjust performance.
	 *
	 * @throws Exception */
	void commitLowLevel() throws Exception;

	/** @return */
	S4WorkerInterface<O, R, L> createGlobalCommonTransaction();

	/** @return */
	S4WorkerTransaction<O, R, L> createNewWorkerTransaction();

	/** @return */
	O createRecordTemplate();

	/** @param key
	 * @param mode
	 * @param target
	 * @return */
	R createReferenceTemplate();

	/** @param pendingRecords
	 * @param targetReferenced
	 * @param targetUnreferenced
	 *
	 * @return
	 * @throws Exception */
	int readCheckReferenced(BasicQueue<O> pendingRecords, //
			BasicQueue<O> targetReferenced,
			BasicQueue<O> targetUnreferenced) throws Exception;

	/**
	 *
	 */
	void reset();

	/** Fills given collection with records that are scheduled to a given tick.
	 *
	 * @param scheduleBits
	 * @param limit
	 * @param targetScheduled
	 * @throws Exception */
	void searchScheduled(short scheduleBits, int limit, BasicQueue<O> targetScheduled) throws Exception;

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
