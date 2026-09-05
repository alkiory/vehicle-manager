# Current Task

# Current Task

- **Task:** EPIC-006 — Statistics & Analytics Engine
- **State:** DONE
- **Objective:** Generate offline period-based financial, mileage, consumption, and fuel-price analytics.
- **Completed:** Added `CalculateVehicleStatsUseCase`, period filtering, integer-safe ownership metrics, weighted monthly fuel prices, monthly expenditure series, reactive `StatisticsViewModel`, period chips, metric cards, Canvas charts, and tests.

## Verification

- `./gradlew testDebugUnitTest compileDebugAndroidTestKotlin --no-daemon --max-workers=1 --console=plain` passes.
- JVM tests cover period boundaries, all-time aggregation, total cost, cost per kilometer, average monthly spend, distance, weighted fuel prices, chart series, zero-distance safety, and reactive period selection.
- Android test sources compile successfully; instrumented execution still requires a connected Android device or emulator.

## Next Task

- EPIC-007 — Polish, Data Export & Release Preparation
