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

## EPIC-008 — Visual Polish & Fluent Icon System Integration

### TASK-001 — FluentUI Color Icons Dependency & Theme Integration (DONE)

- Added `com.microsoft.design:fluent-system-icons:1.1.260` dependency to Gradle version catalog for Android vector drawable resources.
- Created `AppIcons.kt` in `core/ui/theme` with centralized icon mappings for:
  - Navigation destinations (Home, Vehicles, Fuel, Maintenance, Settings)
  - Quick action FAB icons (Plus, Fuel, Wrench, Chart)
  - Feature icons (FuelDrop, Wrench, Trending, VehicleBadge)
- Uses Material Icons Extended as primary Compose icon source (compatible icon coverage)
- Documented Fluent icon integration options:
  - KMP library (io.github.niyajali:fluentui-system-icons) requires multiplatform setup
  - SVG download approach from composables.com for colored icons
- Verification: `./gradlew assembleDebug testDebugUnitTest` — builds and tests pass.

### TASK-002 — App Navigation & Scaffold Visual Upgrade (DONE)

- Replaced Material 3 default icons in `AppScaffold.kt` with Fluent-style icons from `AppIcons`
  - Navigation destinations: NavHome, NavVehicles, NavFuel, NavMaintenance, NavSettings
  - Icons tinted with primary color when selected, onSurfaceVariant when unselected
- Refined bottom bar visual styling:
  - Pill indicator uses primaryContainer with 50% alpha for softer appearance
  - Label typography uses labelSmall style with proper contrast hierarchy
  - Icon size standardized to 24dp
- Updated NavigationRail with same styling
- Removed unused Material Icons imports, consolidated icon references through `AppIcons`
- Verification: `./gradlew assembleDebug testDebugUnitTest` — builds and tests pass.

### TASK-003 — Dashboard Cards & Floating Quick Action Styling (DONE)

- Updated DashboardScreen cards to use Fluent-style icons from `AppIcons`:
  - VehicleHeroCard: `AppIcons.IconVehicleBadge` (vehicle icon)
  - QuickSummaryCard - Combustible: `AppIcons.IconFuelDrop`
  - QuickSummaryCard - Mantenimiento: `AppIcons.IconWrench`
  - QuickSummaryCard - Coste por km: `AppIcons.IconTrending`
- Updated DashboardActionHub FAB with Fluent action icons:
  - "Añadir repostaje" uses `AppIcons.ActionFuel`
  - "Añadir servicio" uses `AppIcons.ActionWrench`
  - FAB toggle uses `AppIcons.ActionPlus` / `AppIcons.ActionClose`
- Updated other feature screen FABs:
  - FuelHistoryScreen: `AppIcons.ActionFuel`
  - MaintenanceHistoryScreen: `AppIcons.ActionWrench`
  - VehiclesScreen: `AppIcons.ActionPlus` and `AppIcons.IconVehicleBadge`
  - FuelScreen: `AppIcons.ActionFuel` and `AppIcons.IconFuelDrop`
- Verification: `./gradlew assembleDebug testDebugUnitTest` — builds and tests pass.

## EPIC-008 Complete

All tasks in EPIC-008 (Visual Polish & Fluent Icon System Integration) are now complete:
- TASK-001: FluentUI Color Icons Dependency & Theme Integration ✅
- TASK-002: App Navigation & Scaffold Visual Upgrade ✅
- TASK-003: Dashboard Cards & Floating Quick Action Styling ✅

---

## EPIC-009 — Advanced Vehicle Statistics & Insights (DONE)

### TASK-001 — Trend Prediction Engine (DONE)

- Created `PredictFuelTrendsUseCase` in `core/domain`
- Implements linear regression on monthly fuel consumption data
- Generates consumption and cost predictions with confidence intervals
- Confidence levels: LOW, MEDIUM, HIGH based on R-squared value
- Requires at least 3 months of data for predictions
- Data classes: `FuelTrendPrediction`, `HistoricalMonthData`, `PredictionConfidence`

### TASK-002 — Cost Analysis by Vehicle & Period (DONE)

- Created `GetCostAnalysisUseCase` in `core/domain`
- Breaks down costs by vehicle, category, and time period
- Supports any `StatsPeriod` for filtering (LAST_30_DAYS, LAST_6_MONTHS, YEAR_TO_DATE, ALL_TIME)
- Vehicle comparison with cost per km and total cost metrics
- Category breakdown with totals and averages
- Data classes: `CostAnalysis`, `CostByVehicle`, `CostByCategory`, `CostByPeriod`, `VehicleCostComparison`

### TASK-003 — Fuel Price Comparison Over Time (DONE)

- Created `CompareFuelPricesUseCase` in `core/domain`
- Station-by-station price analysis with weighted averages
- Monthly price trend data for charting
- Identifies best and worst price stations
- Price diversity metric for station comparison
- Data classes: `FuelPriceComparison`, `StationPriceAnalysis`, `MonthlyPricePoint`, `PriceTrendPoint`, `PeriodStationPrice`

**Verification:** `./gradlew testDebugUnitTest assembleDebug` — builds and tests pass.

---

## Upcoming Epics

### EPIC-010 — Notifications & Reminders (DONE)

### TASK-001 — Service Reminder Notifications (DONE)

- Created `NotificationManagerImpl` for Android notification handling with support for:
  - Maintenance reminder notifications with vehicle name, service title, due info
  - Fuel price alert notifications with station and price info
  - Separate notification channels for different notification types
- Created `ServiceReminderWorker` (WorkManager) for periodic notification checks:
  - Runs every 12 hours to check for due services
  - Shows notifications for overdue and upcoming services
  - Tracks triggered notifications to avoid duplicates
- Created `MaintenanceNotificationReceiver` for handling notification dismiss actions
- Created domain models: `NotificationSchedule`, `ScheduleNotificationRequest`, `NotificationPreferences`, `DueServiceNotification`, `ServiceStatus`
- Created `GetDueServicesUseCase` for detecting due/upcoming services
- Created `NotificationRepository` with Room persistence for notification state
- Added database migration v5 for notification tables
- Added Android permissions: POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED
- Added localization strings for notifications
- Updated `MaintenanceScheduleRepository` with `observeSchedulesForActiveVehicle()` method

### TASK-002 — Fuel Price Alert System (DONE)

- Created `FuelPriceAlertUseCase` for checking price alerts against user threshold
- Integrated with existing `FuelRecordRepository` via new `observeRecentPrices()` method
- Added support for custom price threshold preferences
- Users can enable/disable fuel price alerts
- Alert triggers when price is below user-set threshold
- Uses `NotificationRepository` for preference storage

### TASK-003 — Maintenance Reminder Integration (DONE)

- Created `ReminderManagementUseCase` for snoozing and rescheduling reminders
- Created `SnoozedReminder` and `SnoozeReminderRequest` domain models
- Supports snoozing reminders for a specified duration
- Supports rescheduling reminders to a new date
- Supports dismissing reminders permanently
- Integrates with existing `NotificationRepository` for persistence
- Added `NotificationDao.getActiveNotification()` query for checking active notifications

**Verification:** `./gradlew assembleDebug testDebugUnitTest` — builds and tests pass.

---

---

## EPIC-010 Bug Fixes

### Fuel Odometer Persistence Fix

- Fixed bug where vehicle odometer was not updating after saving a fuel record
- `AddFuelViewModel` now updates `primaryOdometerKm` when saved fuel record has higher odometer
- Added `VehicleRepository` dependency to `AddFuelViewModel`
- Behavior now matches `AddMaintenanceViewModel` which already had this logic
- Verification: `./gradlew assembleDebug testDebugUnitTest` — builds and tests pass

---

## EPIC-010 UI Enhancement

### Fuel Form Total Cost Field

- Added visible `totalCost` input field to `AddFuelScreen` refueling form
- Field order updated: Odometer → Price/Liter → Total Cost → Liters
- Auto-calculated fields show as read-only/disabled to indicate auto-calculation
- Verification: `./gradlew assembleDebug testDebugUnitTest` — builds and tests pass

---

---

## Navigation Enhancement

### Statistics Tab Added to Main Navigation

- Added "Estadísticas" tab to bottom navigation bar and navigation rail
- Uses `AppIcons.NavChart` (TrendingUp icon) for the tab
- Provides access to statistics screen with:
  - Period selector: 30 días, 6 meses, Este año, Todo
  - Resumen card: Coste total, Combustible, Mantenimiento, Distancia, Coste/km, Gasto mensual medio
  - Gráfico de gasto mensual (barras)
  - Gráfico de tendencia de precio del combustible (líneas)
- Statistics screen already existed in `feature/statistics/StatisticsScreen.kt` but was not accessible from main navigation
- Verification: `./gradlew assembleDebug testDebugUnitTest` — builds and tests pass

---

## Next Steps

### EPIC-011 — (TBD)
- Future enhancements can be added here
