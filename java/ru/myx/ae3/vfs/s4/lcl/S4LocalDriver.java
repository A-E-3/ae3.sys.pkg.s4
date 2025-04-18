package ru.myx.ae3.vfs.s4.lcl;

import ru.myx.ae3.help.Convert;
import ru.myx.ae3.reflect.ReflectionEnumerable;
import ru.myx.ae3.reflect.ReflectionExplicit;
import ru.myx.ae3.reflect.ReflectionManual;
import ru.myx.ae3.vfs.s4.common.ArsTransactionS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.common.S4StoreType;
import ru.myx.ae3.vfs.s4.impl.S4TreeDriver;

/** TODO: remove, make mounts configurable through public and protected
 *
 * @author myx */
@ReflectionManual
public class S4LocalDriver
		extends //
			S4DriverLocalAbstract {

	/** @param driverImplClassName
	 * @param type
	 * @return
	 * @throws Exception */
	public static final S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> prepareVolume(final String driverImplClassName, final S4StoreType type) throws Exception {

		final String lclClass = System.getProperty("ru.myx.s4.lcl.class", driverImplClassName);
		final String lclNxtClass = System.getProperty("ru.myx.s4." + type.toString().toLowerCase() + ".nxt.class", lclClass);
		final Class<?> cls = Class.forName(lclNxtClass);
		final S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> driver = Convert.Any.toAny(cls.getConstructor().newInstance());
		return driver;
	}

	/** ru.myx.ae3.vfs.s4.lcl.bdbj.BdbjLocalS4
	 *
	 * @param type
	 * @throws Exception */
	public S4LocalDriver(final S4StoreType type) throws Exception {
		
		super(S4LocalDriver.prepareVolume("ru.myx.ae3.vfs.s4.lcl.bdbj.BdbjLocalS4", type), type);
	}

	/** @param driver
	 * @param type
	 * @throws Exception */
	@ReflectionExplicit
	public S4LocalDriver(final S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> driver, final S4StoreType type) throws Exception {
		
		super(driver, type);
	}

	/** @param driverImplClassName
	 * @param type
	 * @throws Exception */
	@ReflectionExplicit
	public S4LocalDriver(final String driverImplClassName, final S4StoreType type) throws Exception {
		
		super(S4LocalDriver.prepareVolume(driverImplClassName, type), type);
	}

	@Override
	public ArsTransactionS4 createUnderlyingTransaction() {

		return new S4LocalXct(this);
	}

	/** @return */
	@ReflectionExplicit
	@ReflectionEnumerable
	public String getClassName() {

		return this.getClass().getSimpleName();
	}

	/** @return */
	@ReflectionExplicit
	@ReflectionEnumerable
	public S4TreeDriver<?, ?, ?> getTreeDriverImpl() {

		return this.driver;
	}
}
