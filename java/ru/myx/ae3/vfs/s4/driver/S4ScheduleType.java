package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.Engine;

/** <ul>
 * Intervals:
 * <li>fresh, set 1, 12 hours, count: 12*60=720, offset: 0</li>
 * <li>fresh, set 2, 12 hours, count: 12*60=720, offset: 720</li>
 * <li>fresh, set 3, 12 hours, count: 12*60=720, offset: 720+720=1440</li>
 * <li>normal, set 1, 1 week, count: 7*1440=10080, offset: 1440+720=2160</li>
 * <li>normal, set 2, 1 week, count: 7*1440=10080, offset: 2160+10080=12240</li>
 * <li>normal, set 3, 1 week, count: 7*1440=10080, offset: 12240+10080=22320</li>
 * <li>reserved, count: 32767-32400=367, offset: 22320+10080=32400</li>
 * <li>root, count: 1, offset: 32400+367=32767</li>
 * </ul>
 * 
 * There are several 'slots' in 'schedule' value, every slot provides per-minute allocation.
 * 
 * On of three sets of 'normal' schedules are picked randomly on store. Each of them run in 20
 * seconds offset.
 * 
 * Fresh records are scheduled using the first interval, the different procedure is executed for
 * fresh records. */
public enum S4ScheduleType {
	/**
	 * 
	 */
	T00_TWELVE_HOURS_FRESH_SET1 {
		
		@Override
		public short scheduleTicks() {
			
			return 12 * 60;
		}
	},
	/**
	 * 
	 */
	T01_TWELVE_HOURS_FRESH_SET2 {
		
		@Override
		public short scheduleTicks() {
			
			return 12 * 60;
		}
	},
	/**
	 * 
	 */
	T02_TWELVE_HOURS_FRESH_SET3 {
		
		@Override
		public short scheduleTicks() {
			
			return 12 * 60;
		}
	},
	/**
	 * 
	 */
	T03_SEVEN_DAYS_SET1 {
		
		@Override
		public short scheduleTicks() {
			
			return 7 * 24 * 60;
		}
	},
	/**
	 * 
	 */
	T04_SEVEN_DAYS_SET2 {
		
		@Override
		public short scheduleTicks() {
			
			return 7 * 24 * 60;
		}
	},
	/**
	 * 
	 */
	T05_SEVEN_DAYS_SET3 {
		
		@Override
		public short scheduleTicks() {
			
			return 7 * 24 * 60;
		}
	},
	/**
	 * 
	 */
	T06_RESERVED {
		
		@Override
		public short scheduleTicks() {
			
			return 367;
		}
	},
	/**
	 * 
	 */
	T07_ROOT {
		
		@Override
		public short scheduleTicks() {
			
			return 1;
		}
	},;
	
	/**
	 * 
	 */
	public static final short FRESH_START_INCLUDING;
	
	/**
	 * 
	 */
	public static final short FRESH_STOP_EXCLUDING;
	
	/**
	 * 
	 */
	public static final short REFERENCED_START_INCLUDING;
	
	/**
	 * 
	 */
	public static final short REFERENCED_STOP_EXCLUDING;
	
	static {
		
		final S4ScheduleType[] values = S4ScheduleType.values();
		int currentOffset = 0;
		for (final S4ScheduleType value : values) {
			value.offset = (short) currentOffset;
			currentOffset += value.scheduleTicks();
		}
		
		FRESH_START_INCLUDING = T00_TWELVE_HOURS_FRESH_SET1.baseOffset();
		
		FRESH_STOP_EXCLUDING = T03_SEVEN_DAYS_SET1.baseOffset();
		
		REFERENCED_START_INCLUDING = T03_SEVEN_DAYS_SET1.baseOffset();
		
		REFERENCED_STOP_EXCLUDING = T06_RESERVED.baseOffset();
		
		assert T07_ROOT.offset == 32767 : "Root offset is: " + T07_ROOT.offset;
		
		assert S4ScheduleType.REFERENCED_START_INCLUDING == T03_SEVEN_DAYS_SET1.baseOffset();
		assert S4ScheduleType.REFERENCED_STOP_EXCLUDING == T05_SEVEN_DAYS_SET3.baseOffset() + T05_SEVEN_DAYS_SET3.scheduleTicks();
		assert S4ScheduleType.REFERENCED_STOP_EXCLUDING == T06_RESERVED.baseOffset();
	}
	
	private static S4ScheduleType[] FRESH = new S4ScheduleType[]{
			//
			T00_TWELVE_HOURS_FRESH_SET1, T01_TWELVE_HOURS_FRESH_SET2, T02_TWELVE_HOURS_FRESH_SET3,
			//
	};
	
	private static S4ScheduleType[] REFERENCED = new S4ScheduleType[]{
			//
			T03_SEVEN_DAYS_SET1, T04_SEVEN_DAYS_SET2, T05_SEVEN_DAYS_SET3,
			//
	};
	
	/** @param realTicks
	 * @param scheduleTicks
	 * @return */
	public static final short getScheduledFresh(final int realTicks, final int scheduleTicks) {
		
		final S4ScheduleType slot = S4ScheduleType.FRESH[realTicks % 3];
		final int index = scheduleTicks % slot.scheduleTicks();
		return (short) (slot.offset + index);
	}
	
	/** @param realTicks
	 * @param scheduleTicks
	 * @return */
	public static final short getScheduledReferenced(final int realTicks, final int scheduleTicks) {
		
		final S4ScheduleType slot = S4ScheduleType.REFERENCED[realTicks % 3];
		final int index = scheduleTicks % slot.scheduleTicks();
		return (short) (slot.offset + index);
	}
	
	/** @return */
	public static final S4ScheduleType randomFreshSlot() {
		
		return S4ScheduleType.FRESH[Engine.createRandom(3)];
	}
	
	/** @return */
	public static final S4ScheduleType randomReferencedSlot() {
		
		return S4ScheduleType.REFERENCED[Engine.createRandom(3)];
	}
	
	private short offset;
	
	/** @return */
	public final short baseOffset() {
		
		return this.offset;
	}
	
	/** @return */
	public abstract short scheduleTicks();
}
