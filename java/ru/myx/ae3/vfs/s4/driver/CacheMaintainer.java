package ru.myx.ae3.vfs.s4.driver;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import ru.myx.ae3.Engine;
import ru.myx.ae3.act.ActService;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;

final class CacheMaintainer //
		implements
			ActService {

	private boolean active = true;

	private final S4DriverAbstract driver;

	private final ReferenceQueue<Value<RecImpl>> queue;

	private final TaskLocalLifecycleCheck taskStorageCheck;

	CacheMaintainer(//
			final S4DriverAbstract driver,
			final ReferenceQueue<Value<RecImpl>> queue) {

		this.driver = driver;
		this.queue = queue;
		this.taskStorageCheck = new TaskLocalLifecycleCheck();
	}

	@Override
	public boolean main() {

		try {
			for (; this.active;) {
				final long current = Engine.fastTime();
				if (this.taskStorageCheck.executed + 10_000L < current && this.taskStorageCheck.state == TaskLocalLifecycleCheck.TaskState.READY) {
					this.taskStorageCheck.executed = current;
					synchronized (this.taskStorageCheck) {
						this.driver.enqueue(this.taskStorageCheck);
						/** TODO: spurious wakeups */
						this.taskStorageCheck.wait(0L);
					}
				}
				{
					Reference<? extends Value<RecImpl>> reference = this.queue.remove(1500L);
					if (reference == null) {
						continue;
					}
					final long loopStart = System.currentTimeMillis();
					int count = 0;
					{
						{
							final Guid guid = ((CacheRecord) reference).getGuid();
							if (guid != null) {
								this.driver.recordsByGuid.remove(guid);
								++count;
							}
						}
						for (;;) {
							reference = this.queue.poll();
							if (reference == null) {
								break;
							}
							final Guid guid = ((CacheRecord) reference).getGuid();
							if (guid != null) {
								this.driver.recordsByGuid.remove(guid);
								++count;
							}
						}
					}
					if (count > 0) {
						if (Report.MODE_DEBUG) {
							StorageImplS4.log(
									EventLevel.VERBOSE,
									"MAINTENANCE",
									"DISCARD",
									"memory cache, discarded " + count + " record(s) in " + Format.Compact.toPeriod(System.currentTimeMillis() - loopStart));
						}
					}
				}
			}
		} catch (final InterruptedException e) {
			this.active = false;
			return false;
		}
		return this.active;
	}

	@Override
	public boolean start() {

		this.active = true;
		return true;
	}

	@Override
	public boolean stop() {

		this.active = false;
		return false;
	}

	@Override
	public String toString() {

		return "VFS/S4/CACHE-MTN";
	}

	@Override
	public boolean unhandledException(final Throwable t) {

		StorageImplS4.log(EventLevel.ERROR, "MAINTENANCE", "UNHANDLED", Format.Throwable.toText(t));
		try {
			Thread.sleep(5_000L);
		} catch (final InterruptedException e) {
			return false;
		}
		return this.active;
	}
}
