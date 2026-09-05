# Current Task

- **Task:** EPIC-007 — Polish, Data Export & Release Preparation
- **State:** DONE
- **Objective:** Provide validated JSON backup/restore, SAF data portability, accessibility polish, localization groundwork, and release verification.
- **Completed:** Added versioned full-database JSON export/import with validation and transactional Room replacement, active-vehicle restoration, SAF file actions, localized backup strings, chart/action accessibility descriptions, R8 rules, resource shrinking, and release build configuration.

## Verification

- `./gradlew check assembleRelease --no-daemon --max-workers=1 --console=plain` passes.
- JVM tests cover backup round-trip preservation and rejection before mutation.
- Debug lint/check and Android test-source compilation pass.
- Release R8 minification, resource shrinking, and APK assembly pass.
- Gradle reports a non-fatal dependency native-library strip warning for `libandroidx.graphics.path.so` and `libdatastore_shared_counter.so`; those libraries are packaged unchanged.

## Next Task

- Project release-ready; no backlog items remain.
