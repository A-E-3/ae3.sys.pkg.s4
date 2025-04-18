package ru.myx.ae3.vfs.s4.common;

import java.util.Comparator;
import java.util.Map;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.ars.ArsRecord;

/** @author myx */
public abstract class RecImpl //
		implements
			Value<RecImpl>,
			ArsRecord,
			Comparable<RecImpl> {
	
	/** Compares GUIDs as byte keys */
	public static final Comparator<? super RecImpl> COMPARATOR_RECORD_GUID = new Comparator<>() {
		
		@Override
		public int compare(final RecImpl o1, final RecImpl o2) {
			
			return o1.guid.compareTo(o2.guid);
		}
	};
	
	/** Indicates that this record is both: 1) not stored 2) not in-line */
	public static final short RT_TEMPLATE = 0x0001;
	
	/** 31 higher bits of expected schedule number according to an average schedule tick of 60
	 * seconds.
	 *
	 * @param scheduleTicks
	 *            current time in ticks
	 * @param date
	 *            java date (milliseconds)
	 * @return schedule estimated schedule index for given date */
	public static final int convertDateToSchedule(final int scheduleTicks, final long date) {
		
		return (int) ((date - Engine.fastTime()) / 60_000) + scheduleTicks << 1;
	}
	
	/** 31 higher bits of expected schedule number according to an average schedule tick of 60
	 * seconds.
	 *
	 * @param scheduleTicks
	 *            current time in ticks
	 * @param schedule
	 * @return estimated date for given schedule index */
	public static final long convertScheduleToDate(final int scheduleTicks, final int schedule) {
		
		return ((schedule >> 1) - scheduleTicks) * 60_000L + Engine.fastTime();
	}
	
	/** In S4 records are always identified by their GUID. */
	public Guid guid;
	
	/** TODO; remove or use for something (like notifiers) */
	public Map<Guid, RefImpl<RecImpl>> knownReferences;
	
	/** We use it here to store additional state, it is ok, because all java objects are aligned to
	 * 8 byte boundary, so it doesn't change object size.
	 *
	 * Since we have two pointers and two shorts this leaves up extra 4 bytes for something specific
	 * data of this class instances. */
	public short runtimeState;
	
	/** Some magic number that is used by storage scheduler to schedule task within 1 hour and 10
	 * days ranges with 'minutes' granularity. */
	public short scheduleBits;
	
	@Override
	public final RecImpl baseValue() {
		
		return this;
	}
	
	@Override
	public final int compareTo(final RecImpl arg0) {
		
		return this.guid.compareTo(arg0.guid);
	}
	
	@Override
	public final boolean equals(final Object arg0) {
		
		if (arg0 instanceof final RecImpl recImpl) {
			return this.guid.equals(recImpl.guid);
		}
		return false;
	}
	
	@Override
	public final long getBinaryContentLength() {
		
		return this.guid.getBinaryLength();
	}
	
	@Override
	public String getKeyString() {
		
		return String.valueOf(this.guid.getInlineValue());
	}
	
	@Override
	public final BaseObject getPrimitiveBaseValue() {
		
		return this.guid.getInlineBaseValue();
	}
	
	@Override
	public final Guid getPrimitiveGuid() {
		
		return this.guid;
	}
	
	@Override
	public final Object getPrimitiveValue() {
		
		return this.guid.getInlineValue();
	}
	
	@Override
	public final int hashCode() {
		
		return this.guid.hashCode();
	}
	
	@Override
	public final boolean isBinary() {
		
		return this.guid.isBinary();
	}
	
	@Override
	public final boolean isCharacter() {
		
		return this.guid.isInlineString() || this.guid.isBinaryText();
	}
	
	@Override
	public final boolean isContainer() {
		
		return this.guid.isCollection();
	}
	
	/** Guid us an inline guid. S4 storage is GUID based.
	 *
	 * @return */
	public final boolean isInline() {
		
		return this.guid.isInline();
	}
	
	@Override
	public final boolean isPrimitive() {
		
		return this.guid.isPrimitive();
	}
	
	@Override
	public abstract String toString();
}
