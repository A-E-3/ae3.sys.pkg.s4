package ru.myx.ae3.vfs.s4.common;

import java.io.Closeable;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.reflect.ReflectionEnumerable;
import ru.myx.ae3.reflect.ReflectionExplicit;
import ru.myx.ae3.reflect.ReflectionManual;

/** @author myx */
@ReflectionManual
public abstract class RepairChecker //
		implements
			Closeable {

	/**
	 *
	 */
	@ReflectionExplicit
	@ReflectionEnumerable
	public long checks = 0;

	/**
	 *
	 */
	@ReflectionExplicit
	@ReflectionEnumerable
	public long errors = 0;

	/**
	 *
	 */
	@ReflectionExplicit
	@ReflectionEnumerable
	public long warnings = 0;

	/**
	 *
	 */
	@ReflectionExplicit
	@ReflectionEnumerable
	public long fixes = 0;

	/**
	 *
	 */
	@ReflectionExplicit
	@ReflectionEnumerable
	public boolean isAll = false;

	/**
	 *
	 */
	@ReflectionExplicit
	@ReflectionEnumerable
	public boolean isFull = false;

	/** Do fixes */
	@ReflectionExplicit
	@ReflectionEnumerable
	public boolean isFix = false;

	/** Allow/Prefer purge */
	@ReflectionExplicit
	@ReflectionEnumerable
	public boolean isPurge = false;

	/** Allow/Prefer recover */
	@ReflectionExplicit
	@ReflectionEnumerable
	public boolean isRecover = false;

	/** Output progress detail */
	@ReflectionExplicit
	@ReflectionEnumerable
	public boolean isVerbose = false;

	/**
	 *
	 */
	@Override
	@ReflectionExplicit
	public void close() {

		//
	}

	/** @return */
	@ReflectionExplicit
	public abstract boolean next();

	/** @param properties */
	public void setup(final BaseObject properties) {

		this.checks = 0;
		this.errors = 0;
		this.warnings = 0;
		this.fixes = 0;

		this.isAll = Base.getBoolean(properties, "all", false);
		this.isVerbose = Base.getBoolean(properties, "verbose", false);
		this.isFull = Base.getBoolean(properties, "full", false);
		this.isFix = Base.getBoolean(properties, "fix", false);
		this.isRecover = Base.getBoolean(properties, "recover", false);
		this.isPurge = Base.getBoolean(properties, "purge", false);
	}
}
