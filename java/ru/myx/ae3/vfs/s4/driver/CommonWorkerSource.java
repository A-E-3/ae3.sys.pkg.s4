package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.flow.ObjectSource;

/** @author myx */
public class CommonWorkerSource //
		implements
			ObjectSource<CommonWorker> {

	private final S4DriverAbstract local;

	/** @param local */
	public CommonWorkerSource(final S4DriverAbstract local) {

		this.local = local;
	}

	@Override
	public boolean isExhausted() {

		return false;
	}

	@Override
	public boolean isReady() {

		return true;
	}

	@Override
	public CommonWorker next() {

		return new CommonWorker(this.local);
	}
}
