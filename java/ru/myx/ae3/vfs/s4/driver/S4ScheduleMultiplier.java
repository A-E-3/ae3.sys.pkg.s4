package ru.myx.ae3.vfs.s4.driver;

/** Schedule loop divider.
 * <ul>
 * Intervals:
 * <li>Normal is 1x, full loop</li>
 * <li>DOUBLE is 2x, two halves of a full loop at a time, twice shorter cycle</li>
 * <li>DOUBLE is 4x, two halves of a full loop at a time, twice quarter cycle</li>
 * </ul>
*/
public enum S4ScheduleMultiplier {
	/**
	 *
	 */
	M00_NORMAL {

		@Override
		public int loopDivider() {

			return 1;
		}
	},
	/**
	 *
	 */
	M01_DOUBLE {

		@Override
		public int loopDivider() {

			return 2;
		}
	},
	/**
	 *
	 */
	M02_SEVEN {

		@Override
		public int loopDivider() {

			return 7;
		}
	},;

	static {
		for (final S4ScheduleMultiplier multiplier : S4ScheduleMultiplier.values()) {
			assert 7 * 24 * 60 / multiplier.loopDivider() * multiplier.loopDivider() == 7 * 24 * 60 : "Invalid result: multiplier: " + multiplier;
		}
	}

	/** @return */
	public abstract int loopDivider();
}
