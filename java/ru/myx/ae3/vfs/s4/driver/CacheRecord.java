package ru.myx.ae3.vfs.s4.driver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.myx.ae3.common.Value;
import ru.myx.ae3.know.Guid;
import ru.myx.ae3.vfs.s4.common.RecImpl;

interface CacheRecord {

	enum Mode {

		/** when access is localized and memory allocation is a lot comparing to dataset size */
		SOFT {

			@Override
			public void create(final S4DriverAbstract local, final Guid guid, final Value<RecImpl> record) {

				final CacheRecord oldRecord = local.recordsByGuid.put(guid, new CacheRecordSoft(guid, record, local.recordsQueue));
				if (oldRecord != null) {
					oldRecord.clear();
				}
			}

			@Override
			public Map<Guid, CacheRecord> createCache() {

				return new ConcurrentHashMap<>(512);
			}
		},
		/** when access is random and memory allocation is tight comparing to dataset size */
		WEAK {

			@Override
			public void create(final S4DriverAbstract local, final Guid guid, final Value<RecImpl> record) {

				final CacheRecord oldRecord = local.recordsByGuid.put(guid, new CacheRecordWeak(guid, record, local.recordsQueue));
				if (oldRecord != null) {
					oldRecord.clear();
				}
			}

			@Override
			public Map<Guid, CacheRecord> createCache() {

				return new ConcurrentHashMap<>(512);
			}
		},;

		public final static CacheRecord.Mode getDefaultMode() {

			final String value = java.lang.System.getProperty("ru.myx.ae3.vfs.s4.drv.cache-record.mode", "").trim();
			if (value.length() == 0) {
				return CacheRecord.Mode.WEAK;
			}
			for (final CacheRecord.Mode mode : CacheRecord.Mode.values()) {
				if (value.equalsIgnoreCase(mode.name())) {
					return mode;
				}
			}
			throw new IllegalArgumentException("Invalid CacheRecord.Mode: " + value);
		}

		public abstract void create(final S4DriverAbstract local, final Guid guid, final Value<RecImpl> record);

		public abstract Map<Guid, CacheRecord> createCache();
	}

	void clear();

	Value<RecImpl> get();

	Guid getGuid();

	@Override
	String toString();
}
