package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.Engine;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.report.EventLevel;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.s4.StorageImplS4;
import ru.myx.ae3.vfs.s4.common.RecImpl;
import ru.myx.ae3.vfs.s4.common.RecInline;
import ru.myx.ae3.vfs.s4.common.RefImpl;

/** @author myx */
public final class TaskCommonLifecycleStart //
		extends
			TaskCommon<RefImpl<RecImpl>> {
	
	/**
	 *
	 */
	public TaskCommonLifecycleStart() {
		
		//
	}
	
	@Override
	public void execute(final S4WorkerContext context) throws Exception {
		
		{
			/** started in S4DriverLocalAbstract */
			// this.driver.start();
			StorageImplS4.log(EventLevel.INFO, "S4LFCYCL:START", "START", "Storage started, size: " + Format.Compact.toBytes(context.driver.storageCalculate()));
		}

		final RefImpl<RecImpl> rootReference;
		S4WorkerTransaction<RecImpl, RefImpl<RecImpl>, ?> xct = context.createNewWorkerTransaction();
		try {
			RecImpl rootRecord = xct.readRecord(Guid.GUID_NULL);
			{
				if (rootRecord == null) {
					rootRecord = context.worker.createRecordTemplate();
					assert (rootRecord.runtimeState & RecImpl.RT_TEMPLATE) == 0 : "Usage of system-reserved bits!";
					rootRecord.guid = Guid.GUID_NULL;
					// root record is not intended for schedule
					rootRecord.scheduleBits = S4ScheduleType.T07_ROOT.baseOffset();
					xct.arsRecordUpsert(rootRecord);
					rootRecord.runtimeState &= ~RecImpl.RT_TEMPLATE;
				}
				rootRecord.guid = Guid.createGuid184();
				context.local.rootRecord = rootRecord;
			}
			{
				final RefImpl<RecImpl> schedule = xct.readContainerElement(rootRecord, S4Driver.KEY_SCHEDULE_TICKS);
				if (schedule == null) {
					context.local.scheduleTicks = 0;
					xct.arsLinkUpsert(rootRecord, S4Driver.KEY_SCHEDULE_TICKS, TreeLinkType.LOCAL_PRIVATE_REFERENCE, Engine.fastTime(), Guid.GUID_NUMBER_ZERO);
				} else {
					context.local.scheduleTicks = Convert.Any.toInt(schedule.value.getInlineValue(), 0);
				}
			}
			{
				final RefImpl<RecImpl> mounted = xct.readContainerElement(rootRecord, S4Driver.KEY_MOUNTED);
				if (mounted == null) {
					xct.arsLinkUpsert(rootRecord, S4Driver.KEY_MOUNTED, TreeLinkType.LOCAL_PRIVATE_REFERENCE, Engine.fastTime(), Guid.GUID_BOOLEAN_TRUE);
					StorageImplS4.log(EventLevel.INFO, "S4LCL-START", "MOUNT", "mount property created.");
				} else {
					final boolean isMounted = Convert.Any.toBoolean(mounted.value.getInlineValue(), false);
					if (isMounted) {
						StorageImplS4.log(EventLevel.WARNING, "S4LCL-START", "MOUNT", "was not cleanly dismounted.");
					} else {
						xct.arsLinkUpsert(rootRecord, S4Driver.KEY_MOUNTED, TreeLinkType.LOCAL_PRIVATE_REFERENCE, Engine.fastTime(), Guid.GUID_BOOLEAN_TRUE);
						StorageImplS4.log(EventLevel.INFO, "S4LCL-START", "MOUNT", "mount property set to TRUE.");
					}
				}
			}
			{
				rootReference = context.driver.createReferenceTemplate();
				rootReference.driver = context.local;
				rootReference.collection = null;
				rootReference.key = Guid.GUID_NULL;
				rootReference.keyRecord = new RecInline(Guid.GUID_NULL);
				rootReference.mode = TreeLinkType.LOCAL_PRIVATE_REFERENCE;
				rootReference.modified = 0L;
				rootReference.value = Guid.GUID_NULL;
				rootReference.valueRecord = rootRecord;
				context.local.rootReference = rootReference;

				xct.commit();
				xct = null;
			}
		} finally {
			if (xct != null) {
				xct.cancel();
				xct = null;
			}
		}

		{
			context.local.executeDriverStart();
		}

		{
			context.worker.commitLowLevel();
			context.worker.commitHighLevel();
		}

		{
			this.setResult(rootReference);
			StorageImplS4.log(EventLevel.INFO, "S4LFCYCL:START", "ACTIVE", "Storage initialization finished.");
		}
	}
	
	@Override
	public String toString() {
		
		return "TASK_LIFECYCLE_START";
	}
	
	@Override
	protected long getTaskTimeout() {
		
		return 3 * 60_000L;
	}
}
