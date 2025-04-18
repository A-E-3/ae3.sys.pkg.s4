package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.flow.ObjectTarget;

/** @author myx */
public class CommonWorkerRecycler //
		implements
			ObjectTarget<CommonWorker> {

	/**
	 *
	 */
	public static final CommonWorkerRecycler INSTANCE = new CommonWorkerRecycler();

	private CommonWorkerRecycler() {

		//
	}

	@Override
	public boolean absorb(final CommonWorker object) {

		try {
			object.stop();
		} catch (final Exception e) {
			throw new RuntimeException(this.getClass().getSimpleName(), e);
		}
		return true;
	}

	@Override
	public Class<? extends CommonWorker> accepts() {

		return CommonWorker.class;
	}

	@Override
	public void close() {

		//
	}
}
