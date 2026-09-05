# Repository Memory

## Architecture

- Presentation uses Jetpack Compose and ViewModels exposing `StateFlow<UiState>`.
- Domain code must remain pure Kotlin with no Android or Room imports.
- Data code owns Room entities, DAOs, repositories, and entity-to-domain mappers.
- Room is the offline-first canonical source of truth.
- Monetary values use `Long` minor units; fuel quantities use integer liters multiplied by 100; odometers use kilometers.

## Current implementation

- Android namespace and application ID: `com.example.vehiclemanager`.
- The first implementation task is EPIC-001 / TASK-001.

## Conventions established in EPIC-007 / TASK-005

- Fuel amount inputs (liters, price per liter, total cost) are linked by the invariant `Total = Liters × Price/L`; auto-calculation is driven only by user edits (`fuelAmountsEdited` set), never by auto-filled values, to prevent circular update loops.
- All amount conversions and cross-field calculations use `BigDecimal` over persisted minor units (liters ×100, cents) with `HALF_UP` rounding; never floating point.
- Form ViewModels expose `isDirty` in their `UiState` and register it in the singleton `FormDirtyStateHolder` (`core.ui.navigation`); `AppScaffoldViewModel` uses it to gate primary-destination navigation while any form has unsaved changes. Registration must be cleared in `onCleared()`.
- Unsaved-changes confirmation is shared: `UnsavedChangesGuard` (back gesture on form screens) and the AppScaffold dialog reuse the same localized strings (`unsaved_changes_*` in `strings.xml`).
- Backup/restore UX lives exclusively in `SettingsScreen` (Data Management section) and is bound to `VehicleBackupViewModel`; feature screens must not embed SAF launchers for backup.
