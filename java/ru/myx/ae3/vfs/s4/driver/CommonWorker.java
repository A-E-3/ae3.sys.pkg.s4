package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.e4.act.Worker;
import ru.myx.ae3.vfs.s4.StorageImplS4;

class CommonWorker //
		implements
			Worker<TaskCommon<?>> {

	private final S4WorkerContext context;

	CommonWorker(final S4DriverAbstract local) {

		this.context = new S4WorkerContext(local);
		try {
			this.context.worker.start();
		} catch (final RuntimeException t) {
			throw t;
		} catch (final Exception t) {
			local.stsTasksErrors++;
			StorageImplS4.logError("SERVICE-LCL", "exception while starting worker: " + this.context.worker, t);
			throw new RuntimeException(t);
		}
	}

	@Override
	public Object apply(final TaskCommon<?> task) {

		final S4DriverAbstract local = this.context.local;
		try {
			local.stsTasksStarted++;
			task.execute(this.context);
			local.stsTasksFinished++;
		} catch (final Error e) {
			local.stsTasksErrors++;
			StorageImplS4.logError("SERVICE-LCL", "error in task: " + task, e);
			task.setError(e);
		} catch (final Throwable t) {
			local.stsTasksErrors++;
			StorageImplS4.logError("SERVICE-LCL", "exception in task: " + task, t);
			task.setError(new Error(t));
		} finally {
			this.context.worker.reset();
		}
		return null;
	}

	void stop() throws Exception {

		this.context.worker.stop();
	}

	@Override
	public String toString() {

		return this.getClass().getSimpleName() + "[worker:" + this.context.worker + "]";
	}
}
