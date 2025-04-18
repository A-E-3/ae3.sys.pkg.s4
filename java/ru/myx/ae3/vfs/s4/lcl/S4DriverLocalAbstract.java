package ru.myx.ae3.vfs.s4.lcl;

import java.io.File;

import ru.myx.ae3.Engine;
import ru.myx.ae3.e4.act.Manager;
import ru.myx.ae3.e4.act.ManagerService;
import ru.myx.ae3.reflect.ReflectionIgnore;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;
import ru.myx.ae3.vfs.s4.common.S4StoreType;
import ru.myx.ae3.vfs.s4.driver.CommonWorkerRecycler;
import ru.myx.ae3.vfs.s4.driver.CommonWorkerSource;
import ru.myx.ae3.vfs.s4.driver.S4DriverAbstract;
import ru.myx.ae3.vfs.s4.driver.TaskCommon;
import ru.myx.ae3.vfs.s4.driver.TaskCommonLifecycleStart;
import ru.myx.ae3.vfs.s4.driver.TaskCommonLifecycleStop;
import ru.myx.ae3.vfs.s4.impl.S4TreeDriver;

/** @author myx
 *
 *         S4Local is purely synchronized, this class allows to wrap it in concurrent interface. */
@ReflectionIgnore
public abstract class S4DriverLocalAbstract //
		extends
			S4DriverAbstract {

	private static final File DATA_FOLDER;

	static {
		DATA_FOLDER = new File(Engine.PATH_PRIVATE, "data");
		System.out.println("S4LOCAL: private data folder path: " + S4DriverLocalAbstract.DATA_FOLDER.getAbsolutePath());
		S4DriverLocalAbstract.DATA_FOLDER.mkdirs();
	}

	static final ManagerService<TaskCommon<?>> createManager(final S4DriverAbstract local, final S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> driver) {

		return Manager.Factory.getManagerType(driver).createManager(
				"SERVICE-S4DRV-" + local.getStoreType(), //
				new CommonWorkerSource(local),
				CommonWorkerRecycler.INSTANCE //
		);
	}

	private final File extraFolder;

	private final File[] extraLeafs;

	Manager<TaskCommon<?>> performer;

	final ManagerService<TaskCommon<?>> performerService;

	/** @param driver
	 * @param type
	 * @throws Exception */
	public S4DriverLocalAbstract(final S4TreeDriver<RecImpl, RefImpl<RecImpl>, Object> driver, final S4StoreType type) throws Exception {

		super(driver, type);

		this.extraFolder = new File(S4DriverLocalAbstract.DATA_FOLDER, "extra");
		this.extraFolder.mkdirs();
		this.extraLeafs = new File[256];
		for (int i = 255; i > 15; --i) {
			this.extraLeafs[i] = new File(this.extraFolder, Integer.toHexString(i).toUpperCase());
		}
		for (int i = 15; i >= 0; --i) {
			this.extraLeafs[i] = new File(this.extraFolder, '0' + Integer.toHexString(i).toUpperCase());
		}

		this.performerService = S4DriverLocalAbstract.createManager(this, driver);
	}

	@Override
	protected void enqueue(final TaskCommon<?> task) {

		this.performer.enqueue(task);
	}

	@Override
	protected void executeDriverStart() throws Exception {

		super.executeDriverStart();
	}

	@Override
	public String getKey() {

		return this.type.toString() + " " + this.driver.getKey();
	}

	@Override
	public boolean start() throws Exception {

		StorageImplS4.log(EventLevel.NOTICE, "S4DRV:START", "START", "Starting S4 driver: " + this.driver.toString());
		if (!super.start()) {
			return false;
		}
		{
			/** For some unknown reason, sometimes, GC thread doesn't start until memory is
			 * completely full, but GC activity is crucial to keep cache as clear as possible. */
			Runtime.getRuntime().gc();
			Runtime.getRuntime().gc();
		}
		this.driver.start();
		this.performerService.start();
		this.performer = this.performerService.getManager();
		final TaskCommonLifecycleStart start = new TaskCommonLifecycleStart();
		this.rootReference = start;
		this.performer.enqueue(start);
		// join / wait
		start.baseValue();
		StorageImplS4.log(EventLevel.NOTICE, "S4DRV:START", "START", "S4 driver started, driver: " + this.driver.toString());
		return true;
	}

	@Override
	public boolean stop() throws Exception {

		StorageImplS4.log(EventLevel.NOTICE, "S4DRV:STOP", "STOP", "S4 driver stopping...");
		Thread.sleep(50L);

		final TaskCommon<?> task = new TaskCommonLifecycleStop();

		this.performer.enqueue(task);

		// join / wait
		Thread.sleep(50L);
		task.baseValue();

		this.performerService.stop();
		this.driver.stop();
		if (!super.stop()) {
			return false;
		}
		StorageImplS4.log(EventLevel.NOTICE, "S4DRV:STOP", "STOP", "S4 driver stopped.");
		return true;
	}
}
