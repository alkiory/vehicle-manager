# Current Task

- **Task:** EPIC-007 — TASK-005 (Form Auto-Calculations, Unsaved Changes Guard, Backup Migration & Metric Cards Fix)
- **State:** DONE
- **Objective:** Implement smart refueling calculations, unsaved form navigation guards, relocate backup UI to settings, and fix dashboard placeholder metrics.

## Completed

1. **Refueling dynamic calculator** (`AddFuelViewModel` + `FuelForm.kt`): entering any two of `Total Cost` / `Liters` / `Price per Liter` auto-populates the third (`Total = Liters × Price/L`) using BigDecimal math over persisted minor units; auto-filled field refreshes on input edits and clears when inputs become invalid; user-edited values are never overwritten, so no circular update loop is possible. New "Total importe" field added to `AddFuelScreen`.
2. **Unsaved changes guard**: `isDirty` tracked in `AddFuelViewModel`, `AddEditVehicleViewModel` and `AddMaintenanceViewModel` (registered into the singleton `FormDirtyStateHolder`, cleared in `onCleared`). Shared `UnsavedChangesGuard` composable intercepts the system back gesture on dirty form screens; `AppScaffoldViewModel` intercepts bottom-tab/rail navigation while any form is dirty and both paths share one discard-confirmation `AlertDialog` (localized strings added).
3. **Backup relocation**: `DataBackupCard` and its SAF launchers removed from `VehiclesScreen` (now a pure vehicle list); Settings gained a "Gestión de datos" section with Export/Import actions bound to `VehicleBackupViewModel`.
4. **Dashboard metric fix**: fuel summary card shows "Sin datos suficientes" (caption: "Se necesitan dos depósitos llenos") instead of formatting `0.00 L/100km` when full-tank history is insufficient; "Sin registros" only when there is no fuel record at all.

## Verification

- `./gradlew testDebugUnitTest` — 76 tests, 0 failures.
- `./gradlew assembleDebug` — succeeds.
- New tests: `FuelFormTest` (calculator math/rounding/format), `AddFuelViewModelTest` (any-2→3rd, refresh, invalid-clear, no overwrite of user input, save uses auto-filled total, isDirty), `AppScaffoldViewModelTest` (dirty registry, navigation blocking, discard runs pending navigation, dismiss, immediate pass-through when clean).

## Next Task

- None defined; backlog is empty and the project remains release-ready.
