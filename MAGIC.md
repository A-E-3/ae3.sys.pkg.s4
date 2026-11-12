# MAGIC.md — ae3.sys.pkg.s4

## For keeper-ae3

This package owns the S4 storage tier's abstraction and nothing concrete: `driver/` (the worker,
transaction and cache machinery), `common/` (the record and reference shapes), `net/` (the cluster
protocol) and `lcl/` (the local-driver contract a backend implements). It holds 64 `.java` files. Its
`README.md` names `ae3.sys.pkg.s4.lcl.bdbje` as the on-disk implementation, which understates the set
below.

### Twelve S4 directories exist here, and the devel tree holds two

`/Volumes/workspace/myx` holds eleven directories named `ae3.sys.pkg.s4*`: `ae3.sys.pkg.s4` itself, then
`lcl.bdbje`, `lcl.bdbje.incubator`, `lcl.db4o`, `lcl.jdbc`, `lcl.jdbc.drby`, `lcl.jdbc.h2db`,
`lcl.jdbc.hsql`, `lcl.jdbc.incubator`, `lcl.jdbc.psgr` and `lcl.jodb`. A twelfth, `ae3.sys.s4.lcl.jdbc`,
carries the older naming: `src/` with no `.java` in it, `bin/` with 38 classes including `drby/` and
`h2db/`, and no `ae3-packages/`.

`util.repository-ae3`'s `sh-data/repository/remotes-list-ae3.txt` lists two S4 repos, at lines 54 and 55:
`ae3/ae3.sys.pkg.s4` and `ae3/ae3.sys.pkg.s4.lcl.bdbje`. The `ws-myx.ae3-devel` tree holds exactly those
two. The other ten exist only in this place.

### Six descriptors are staged in the axiom, and four driver jars ship beside them

`acm-cvs/sys-current/settings/system/s4/driver/` carries six descriptors: `bdbje`, `db4o`, `h2db`,
`hsql`, `jodb` and `psgr`. `acm-cvs/sys-current/axiom/` carries four S4 driver jars:
`ru.myx.ae3sys.s4.lcl.bdbj-5.0.jar` (62 classes), `...db4o-5.0.jar` (10), `...jdbc-5.0.jar` (33) and
`...jodb-5.0.jar` (6). `acm-cvs/sys-release/axiom/` carries the same four.

`h2db`, `hsql` and `psgr` have no jar in either axiom. Their classes exist elsewhere:
`ae3-share/ae3-distro/common/ae3-classes/ru/myx/ae3/vfs/s4/lcl/` holds the whole staged set, counted
per directory — `bdbj` 48, `bdbj_compare` 7, `db4o` 10, `h2db` 1, `hsql` 1, `jdbc` 1, `jdbc_abstract`
32, `jodb` 6, `psgr` 1. Each backend project's own `bin/` holds its class too, and `h2db` also appears
in `ae3.sys.s4.lcl.jdbc/bin`.

That split is load-bearing. Forcing six impl classes by name, controlled:

| classpath | h2db | hsql | psgr | db4o | jodb | jdbc |
|---|---|---|---|---|---|---|
| the axiom's 51 jars only | not loaded | not loaded | not loaded | loaded | loaded | loaded |
| ...plus `ae3-classes/` | loaded | loaded | loaded | loaded | loaded | loaded |

"Not loaded" is `ClassNotFoundException`. The staged tree alone loads `h2db`, so the second row is not
a pass on an empty path.

A live boot does not put `ae3-classes/` on its classpath, measured 2026-11-09. Booting
`acm-cvs/sys-current` with `s4fs:lcl:h2db` exits 255 with `ClassNotFoundException: Not found:
ru.myx.ae3.vfs.s4.lcl.h2db.H2dbLocalS4`, and `ae3-classes` is in none of the 48 libraries the launcher
loads. So those three descriptors cannot resolve on the shipped axiom as it stands.

Staged as `axiom/ae3-classes-5.0` the class does load, and the driver then fails for a second reason:
`H2dbLocalS4.start` gets no stream for its own `!create-nxt.sql`. Both measurements, and the launcher's
naming rule, are in `ae3-devel-tools/MAGIC.md`.

### The legacy builder names seven backends and excludes one on purpose

`ae3-devel-tools/MAKE-AE3-DISTRO.xml`'s `make_sys_current` target syncs `bin/` trees into
`ae3-classes/`. Its S4 entries are at lines 61 to 69: `s4`, `lcl.bdbje`, `lcl.db4o`, `lcl.jdbc`,
`lcl.jdbc.h2db`, `lcl.jdbc.hsql`, `lcl.jdbc.psgr`, `lcl.jodb`. `lcl.jdbc.drby` is the only one excluded,
at line 65, with its own comment.

Its `packages-all` pass at lines 132 to 140 copies resources and settings for `ae3.sys.s4` and seven
backends: `bdbje`, `db4o`, `jdbc`, `h2db`, `hsql`, `jodb` and `psgr`. `drby` is excluded again at line
135.

The incubator is named nowhere in that file: `grep -c incubator` returns 0. So `qedb`, `sqlt` and
`ssql` reach neither the axiom's `settings/system/s4/driver/` nor its jars, even though the project's
own `bin/` holds their three classes and its `package.json` calls them "Untested and Aplha drivers".

### The test tree is the only record of which backends work, and it marks three dead

`ae3.test/java/ru/myx/ae3/test/vfs/VfsVariants.java` enumerates the backends behind an `isWorking()`
default of `true`. Three override it to `false`:

- `TEMP_S4_SSQL` at lines 116-130, commented `doesn't support ON CASCADE DELETE` and `cannot do
  indexes - so can't read data`.
- `TEMP_S4_QEDB` at lines 131-147, commented with faults in `setBytes`, `setBinaryStream` and opening
  an existing store.
- `TEMP_S4_JODB` at lines 148-161, commented `// hangs`. `JodbLocalS4`'s own class javadoc agrees:
  `FIXME: NOT WORKING, NOT NEEDED, NOT USED`.

`jodb` is the only one of the three that ships: its jar, its staged classes, its descriptor and a
loading class. The other two are incubator-only and never reach the axiom.

`VfsVariants` and `S4Variants.java` both also name `ru.myx.ae3.vfs.s4.lcl.bdbj_classic.BdbjClassic`,
which exists in no project and no `bin/` tree under `/Volumes/workspace/myx`. They name
`s4.lcl.drby.DrbyLocalS4`, which exists as a class only in `ae3.sys.s4.lcl.jdbc/bin`.

### `JdbcLocalS4` is dead scaffolding inside the one backend that does ship

`ae3.sys.pkg.s4.lcl.jdbc/java/ru/myx/ae3/vfs/s4/lcl/jdbc/JdbcLocalS4.java` is 102 lines, three of them
`TODO Auto-generated method stub`, and nothing in the tree references, extends or instantiates it. Its
own javadoc says so at lines 8 to 21. The real backends extend `JdbcAbstractLocalS4` directly. The
class still ships, inside the 33-class `jdbc` jar.
