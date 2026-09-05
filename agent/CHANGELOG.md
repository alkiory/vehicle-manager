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
- Implemented EPIC-003 / TASK-003 active-vehicle fuel history, detail view, swipe-to-delete confirmation, and tests.
- Implemented EPIC-003 / TASK-004 full-tank fuel consumption use case with partial-refuel accumulation and edge-case tests.
- Implemented EPIC-004 / TASK-002 maintenance entry/history UI, active-vehicle persistence, odometer advancement, edit flow, deletion confirmation, and tests.
- Implemented EPIC-004 / TASK-003 maintenance schedules with vehicle ownership, distance/time intervals, Room version 4 migration, cascade deletion, configurable due-soon evaluation, and reminder status tests.
- Implemented EPIC-005 dashboard aggregation, active-vehicle health cards, fuel economy summary, maintenance alert banner, responsive layout, and quick-action hub.
- Implemented EPIC-006 statistics calculations, period filtering, cost and mileage metrics, weighted fuel-price trends, monthly expenditure series, reactive statistics state, period chips, and Canvas charts.
- Implemented EPIC-007 versioned JSON backup/restore, transactional Room replacement, active-vehicle restoration, SAF export/import actions, localized backup strings, accessibility semantics, R8 rules, resource shrinking, and release verification.
- Added repository-level Android/Gradle ignore rules while preserving Room schema history.
- Updated Java and Kotlin compilation targets from JDK 17 to JDK 21.
- Implemented EPIC-007 / TASK-005 refueling dynamic calculator: any two of total/liters/price auto-populate the third with exact minor-unit BigDecimal math, auto-fill refresh and invalid-input clearing, no circular loops; added the total-cost field to the refuel form.
- Implemented EPIC-007 / TASK-005 unsaved-changes guard: isDirty tracking in the three form ViewModels via a singleton FormDirtyStateHolder, back-gesture interception on form screens, tab/rail navigation interception in AppScaffold, and a shared localized discard-confirmation dialog.
- Implemented EPIC-007 / TASK-005 backup relocation: removed DataBackupCard from VehiclesScreen and added the Data Management section with export/import actions in SettingsScreen.
- Implemented EPIC-007 / TASK-005 dashboard metric fix: fuel card shows "Sin datos suficientes" instead of 0.00 L/100km when full-tank history is insufficient.
