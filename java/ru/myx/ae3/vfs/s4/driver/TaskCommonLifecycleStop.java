package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.Engine;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RefImpl;

/** @author myx */
public final class TaskCommonLifecycleStop //
		extends
			TaskCommon<Void> {

	/**
	 *
	 */
	public TaskCommonLifecycleStop() {

		//
	}

	@Override
	public void execute(final S4WorkerContext context) throws Exception {

		{
			StorageImplS4.log(EventLevel.INFO, "S4LFCYCL:STOP", "DEACTIVATE", this.getClass().getSimpleName() + ": trying to stop S4 service.");
		}

		S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, ?> xct = context.createNewWorkerTransaction();
		try {
			xct.arsLinkUpsert(context.local.rootRecord, S4Driver.KEY_MOUNTED, TreeLinkType.LOCAL_PRIVATE_REFERENCE, Engine.fastTime(), Guid.GUID_BOOLEAN_FALSE);
			StorageImplS4.log(EventLevel.INFO, "S4LFCYCL:STOP", "MOUNT", "mount property set to false.");
			xct.commit();
			xct = null;
		} finally {
			if (xct != null) {
				xct.cancel();
			}
		}

		{
			context.worker.commitLowLevel();
			context.worker.commitHighLevel();
		}

		{
			this.setResult(null);
			StorageImplS4.log(EventLevel.INFO, "S4LFCYCL:STOP", "STOPPED", this.getClass().getSimpleName() + ": S4 service stopped.");
		}
	}

	@Override
	public String toString() {

		return "TASK_LIFECYCLE_STOP";
	}
}
