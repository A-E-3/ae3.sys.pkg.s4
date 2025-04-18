package ru.myx.ae3.vfs.s4.common;

import ru.myx.ae3.vfs.s4.driver.S4ScheduleMultiplier;

/**
 *
 */
public enum S4StoreType {
	/**
	 *
	 */
	CACHE {

		@Override
		public boolean allowTruncate() {

			return true;
		}

		@Override
		public int defaultCachePercent() {

			return 25;
		}

		@Override
		public S4ScheduleMultiplier scheduleMultiply() {

			return S4ScheduleMultiplier.M00_NORMAL;
		}

		@Override
		public short scheduleTicksFresh() {

			return 12 * 60;
		}

		@Override
		public boolean storeIndex() {

			return false;
		}

		@Override
		public boolean storeUsage() {

			return false;
		}

		@Override
		public String toString() {

			return "cch";
		}
	},
	/**
	 *
	 */
	LOCAL {

		@Override
		public boolean allowTruncate() {

			return false;
		}

		@Override
		public int defaultCachePercent() {

			return 50;
		}

		@Override
		public S4ScheduleMultiplier scheduleMultiply() {

			return S4ScheduleMultiplier.M00_NORMAL;
		}

		@Override
		public short scheduleTicksFresh() {

			return 4 * 60;
		}

		@Override
		public boolean storeIndex() {

			return true;
		}

		@Override
		public boolean storeUsage() {

			return true;
		}

		@Override
		public String toString() {

			return "lcl";
		}
	},
	/**
	 *
	 */
	NEXT {

		@Override
		public boolean allowTruncate() {

			return false;
		}

		@Override
		public int defaultCachePercent() {

			return 50;
		}

		@Override
		public S4ScheduleMultiplier scheduleMultiply() {

			return S4ScheduleMultiplier.M00_NORMAL;
		}

		@Override
		public short scheduleTicksFresh() {

			return 4 * 60;
		}

		@Override
		public boolean storeIndex() {

			return true;
		}

		@Override
		public boolean storeUsage() {

			return true;
		}

		@Override
		public String toString() {

			return "nxt";
		}
	},
	/**
	 *
	 */
	PREV {

		@Override
		public boolean allowTruncate() {

			return false;
		}

		@Override
		public int defaultCachePercent() {

			return 33;
		}

		@Override
		public S4ScheduleMultiplier scheduleMultiply() {

			return S4ScheduleMultiplier.M00_NORMAL;
		}

		@Override
		public short scheduleTicksFresh() {

			return 12 * 60;
		}

		@Override
		public boolean storeIndex() {

			return true;
		}

		@Override
		public boolean storeUsage() {

			return false;
		}

		@Override
		public String toString() {

			return "prv";
		}
	},
	/**
	 *
	 */
	TEST {

		@Override
		public boolean allowTruncate() {

			return true;
		}

		@Override
		public int defaultCachePercent() {

			return 50;
		}

		@Override
		public S4ScheduleMultiplier scheduleMultiply() {

			return S4ScheduleMultiplier.M02_SEVEN;
		}

		@Override
		public short scheduleTicksFresh() {

			return 5;
		}

		@Override
		public boolean storeIndex() {

			return true;
		}

		@Override
		public boolean storeUsage() {

			return true;
		}

		@Override
		public String toString() {

			return "tst";
		}
	},
	/** Same as TEST but doesn't truncated on start */
	TEST_OPEN {

		@Override
		public boolean allowTruncate() {

			return true;
		}

		@Override
		public int defaultCachePercent() {

			return 33;
		}

		@Override
		public S4ScheduleMultiplier scheduleMultiply() {

			return S4ScheduleMultiplier.M02_SEVEN;
		}

		@Override
		public short scheduleTicksFresh() {

			return 30;
		}

		@Override
		public boolean storeIndex() {

			return true;
		}

		@Override
		public boolean storeUsage() {

			return true;
		}

		@Override
		public String toString() {

			return "tst";
		}
	},;

	/** @param name
	 * @return */
	public static final S4StoreType forName(final String name) {

		for (final S4StoreType type : S4StoreType.values()) {
			if (type.toString().equals(name)) {
				return type;
			}
		}
		return null;
	}

	/** @return */
	public abstract boolean allowTruncate();

	/** amount of memory to use by cached, in percent of available memory
	 *
	 * @return */
	public abstract int defaultCachePercent();

	/** @return */
	public abstract S4ScheduleMultiplier scheduleMultiply();

	/** Minutes, not more than 12 hours of delay.
	 *
	 * @return */
	public abstract short scheduleTicksFresh();

	/** @return */
	public abstract boolean storeIndex();

	/** @return */
	public abstract boolean storeUsage();
}
