# ae3.sys.pkg.s4

"S4" — the clustered/distributed storage driver backing AE3's VFS (`Storage`, see `ae3.api`'s `CLAUDE.md`). Covers the cluster network protocol (`net/`: `S4Network`, `PeerConnection`, `ClusterPoint`/`ClusterSector`), the driver/worker/transaction machinery (`driver/`: `S4Driver`, `S4WorkerTransaction*`, caching via `CacheRecord`/`CacheMaintainer`), and a local-driver abstraction (`lcl/`) that a concrete storage backend plugs into — see `ae3.sys.pkg.s4.lcl.bdbje` for the actual on-disk implementation.
