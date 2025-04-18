package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.reflect.ReflectionExplicit;
import ru.myx.ae3.reflect.ReflectionManual;

@ReflectionManual
final class S4RepairCheckerImpl {

	static enum Stage {
		GUIDS, INIT, TAILS, TREES
	}

	final S4Driver<?, ?, ?, ?> driver;

	Stage stage;

	// RepairCheckScanner scanner;

	S4RepairCheckerImpl(final S4Driver<?, ?, ?, ?> driver) {
		this.stage = Stage.INIT;
		this.driver = driver;
	}

	/** TODO: add progress listener
	 *
	 * @return */
	@ReflectionExplicit
	public boolean next() {

		switch (this.stage) {
			case INIT : {
				// this.scanner = this.driver.createRepairScanner();
				this.stage = Stage.GUIDS;
				return true;
			}
			case GUIDS :
			case TAILS :
			case TREES :
			default :
		}
		return false;
	}
}
