# Changelog

## Initial setup

- Initialized the Android project foundation for EPIC-001 / TASK-001.
- Added Kotlin, Compose, Hilt, KSP, Room, Navigation, Coroutines, and Serialization catalog entries.
- Added Hilt application/activity wiring and architecture package skeleton.
- Implemented EPIC-001 / TASK-002 Material 3 colors, typography, shapes, theme previews, and shared Compose wrappers.
- Implemented EPIC-001 / TASK-003 Room database, converters, initial vehicle DAO, Hilt database module, and converter tests.
- Implemented EPIC-001 / TASK-004 typed navigation routes, adaptive app scaffold, and placeholder feature screens.
- Implemented EPIC-002 / TASK-001 vehicle domain model, Room CRUD data layer, repository mappings, Hilt binding, and tests.
- Implemented EPIC-002 / TASK-002 vehicle add/edit route, ViewModel validation state, Compose form, fuel selection, and persistence flow.
- Implemented EPIC-002 / TASK-003 persistent active vehicle selection with DataStore, automatic fallback, Hilt binding, and tests.
- Implemented EPIC-003 / TASK-001 fuel record domain and Room data layer, cascade migration, vehicle-filtered DAO, repository, and tests.
- Implemented EPIC-003 / TASK-002 refueling form, exact minor-unit calculations, active-vehicle odometer prefill, date picker, and save flow.
- Added repository-level Android/Gradle ignore rules while preserving Room schema history.
- Updated Java and Kotlin compilation targets from JDK 17 to JDK 21.
