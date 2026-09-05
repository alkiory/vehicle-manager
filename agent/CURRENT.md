# Current Task

- **Task:** EPIC-004 / TASK-002 — Service Record Entry & History UI
- **State:** DONE
- **Objective:** Add and review vehicle maintenance records with active-vehicle context.
- **Completed:** Added maintenance form validation and cost conversion, `AddMaintenanceViewModel`, active-vehicle-scoped service persistence, primary odometer advancement, category/date/notes/provider fields, maintenance history list, edit navigation, swipe-to-delete confirmation, and tests.

## Verification

- `./gradlew testDebugUnitTest compileDebugAndroidTestKotlin --no-daemon --max-workers=1 --console=plain` passes.
- JVM tests cover required fields, cost conversion, odometer ordering, and ViewModel persistence behavior.
- Android test sources compile successfully; instrumented execution still requires a connected Android device or emulator.

## Next Task

- EPIC-004 / TASK-003 — Service Reminders & Schedule Engine
