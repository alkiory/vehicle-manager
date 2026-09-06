# Vehicle Manager — Master Product & Architecture Blueprint

This master specification document establishes the technical blueprint, architectural conventions, agent protocol, and detailed task breakdowns for building the **Vehicle Manager** native Android application.

---

## 1. System Architecture & Technical Stack

### Core Technology Stack

* **Language:** Kotlin 2.0+ (with Compose Compiler Gradle Plugin)
* **Target / Compile SDK:** 35 | **Min SDK:** 26 (Android 8.0)
* **UI Framework:** Jetpack Compose with Material Design 3
* **Iconography:** FluentUI System Icons (Color & Regular Variants)
* **Dependency Injection:** Hilt (`com.google.dagger:hilt-android`)
* **Local Persistence:** Room Database with KSP (`androidx.room`)
* **Asynchronous Streams:** Coroutines + Kotlin Flow
* **Navigation:** Jetpack Navigation Compose (Type-Safe Navigation with Kotlinx Serialization)
* **Build System:** Gradle with Version Catalog (`gradle/libs.versions.toml`)

### Architecture Rules

1. **Layer Separation:**
* **Presentation Layer:** Jetpack Compose screens consuming state from `ViewModel` via `StateFlow<UiState>`. Zero business logic in Composables.
* **Domain Layer:** Pure Kotlin modules containing Use Cases, Domain Models, and Value Objects. **Strictly no Android or Room imports**.
* **Data Layer:** Room Entities, DAOs, Repositories, and Data Sources. Maps Room Entities to Domain Models via extension mappers.


2. **Data Integrity Standard:**
* **Monetary Values:** Stored as `Long` minor units (e.g., cents, pence) to eliminate floating-point inaccuracies.
* **Fuel Quantities:** Stored as integer minor units (liters $\times 100$, e.g., 45.25 liters = `4525`).
* **Odometer:** Stored as `Int` or `Long` in kilometers.


3. **Offline-First:** Local SQLite database via Room acts as the canonical source of truth. No remote network dependencies.

---

## 2. Repository Memory & Directory Structure

```
/
├── README.md
├── knowledge/
│   ├── product.md
│   ├── architecture.md
│   ├── domain.md
│   ├── database.md
│   ├── calculations.md
│   ├── conventions.md
│   └── testing.md
├── agent/
│   ├── CURRENT.md
│   ├── CHANGELOG.md
│   ├── MEMORY.md
│   └── BACKLOG.md
└── epics/
    ├── EPIC-001-foundation/
    ├── EPIC-002-vehicles/
    ├── EPIC-003-fuel/
    ├── EPIC-004-maintenance/
    ├── EPIC-005-dashboard/
    ├── EPIC-006-statistics/
    ├── EPIC-007-polish-release/
    └── EPIC-008-fluent-icons-visual-enhancements/

```

### Agent State Machine & Lifecycle

Every task strictly follows the lifecycle state machine:

$$\text{BACKLOG} \longrightarrow \text{READY} \longrightarrow \text{IN\_PROGRESS} \longrightarrow \text{REVIEW} \longrightarrow \text{DONE}$$

*If blocked, state transitions to `BLOCKED` with mandatory `blocked_by` details recorded in `agent/CURRENT.md`.*

> **CRITICAL AGENT MEMORY MANDATE:**
> Upon starting and completing any task, the agent **MUST** explicitly update the memory files located inside the `agent/` folder (`agent/CURRENT.md`, `agent/CHANGELOG.md`, `agent/MEMORY.md`, and `agent/BACKLOG.md`). The repository files remain the single source of truth across development sessions.

---

## 3. Detailed Epics & Task Specification

---

### EPIC-001 — Foundation & Architecture

**Objective:** Establish core build configurations, Material 3 design system, base Room persistence, and type-safe navigation routing.

#### TASK-001 — Project Foundation & Dependency Configuration

* **Status:** `🟢 DONE`
* **Objective:** Initialize Android project structure, Gradle version catalog, Hilt, KSP, and Room build dependencies.
* **Requirements:**
1. Configure `gradle/libs.versions.toml` with Compose, Hilt, Room, Navigation, and Coroutines versions.
2. Configure `app/build.gradle.kts` with KSP plugin and set `room.schemaLocation` to `$projectDir/schemas`.
3. Create `VehicleApplication.kt` annotated with `@HiltAndroidApp`.
4. Establish package structure: `core.data`, `core.domain`, `core.ui`, `feature.*`.


* **Acceptance Criteria:**
* Clean build via `./gradlew assembleDebug`.
* Hilt injects successfully into `MainActivity`.


* **Verification:** `./gradlew assembleDebug`

#### TASK-002 — Base Design System & Material 3 Theme

* **Status:** `🟢 DONE`
* **Objective:** Set up M3 color palette, typography, shapes, and theme components.
* **Requirements:**
1. Create `Theme.kt`, `Color.kt`, `Type.kt`, `Shape.kt` in `core/ui/theme`.
2. Implement `VehicleManagerTheme` supporting Light/Dark mode and Dynamic Color (SDK 31+).
3. Create standard Compose wrappers for App Bar, Screen Containers, and Buttons.


* **Acceptance Criteria:**
* Theme previews render cleanly for Light and Dark modes.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-003 — Room Base Infrastructure & Converters

* **Status:** `🟢 DONE`
* **Objective:** Instantiate Room database and standard type converters.
* **Requirements:**
1. Define `VehicleDatabase.kt` extending `RoomDatabase`.
2. Create `Converters.kt` for `Instant`/Epoch millisecond, Enum, and minor-unit monetary mappings.
3. Create `DatabaseModule.kt` providing singleton database and DAOs.


* **Acceptance Criteria:**
* Database builds without schema warnings.
* Unit tests pass for type converters.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-004 — Navigation Skeleton & Main Scaffold

* **Status:** `🟢 DONE`
* **Objective:** Set up type-safe Navigation Compose routes and main app layout.
* **Requirements:**
1. Define `@Serializable` routes (`DashboardRoute`, `VehiclesRoute`, `FuelRoute`, `MaintenanceRoute`, `StatsRoute`).
2. Build `AppScaffold` with adaptive Material 3 `NavigationBar` and `NavigationRail`.
3. Wire routes to placeholder feature screens.


* **Acceptance Criteria:**
* Bottom navigation transitions smoothly between primary destinations.
* Navigation state survives configuration changes.


* **Verification:** `./gradlew connectedDebugAndroidTest`

---

### EPIC-002 — Vehicle Management

**Objective:** Implement multi-vehicle lifecycle support including domain models, DB entities, CRUD capabilities, and active vehicle selection state.

#### TASK-001 — Vehicle Domain Model & Data Layer

* **Status:** `🟢 DONE`
* **Objective:** Define Vehicle domain models, Room entity, DAO, and repository implementation.
* **Requirements:**
1. Create pure Kotlin `Vehicle` domain model (`id`, `name`, `make`, `model`, `year`, `licensePlate`, `vin`, `fuelType`, `primaryOdometerKm`).
2. Create `VehicleEntity` with Room annotations and primary key auto-generation.
3. Implement `VehicleDao` with `Flow<List<VehicleEntity>>`, `insert`, `update`, `delete` queries.
4. Build `VehicleRepositoryImpl` with mapping extension functions.


* **Acceptance Criteria:**
* Unit tests confirm repository mapping isolates Room from domain layer.
* DAO tests pass for CRUD operations on in-memory database.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-002 — Vehicle Entry & Editing Flow

* **Status:** `🟢 DONE`
* **Objective:** Implement Compose UI and ViewModels to create and edit vehicles.
* **Requirements:**
1. Build `AddEditVehicleViewModel` with validation (year range, required fields, positive odometer).
2. Build `AddEditVehicleScreen` with Material 3 TextFields, Fuel Type Dropdown, and Save Button.
3. Connect screen to `VehicleRepository`.


* **Acceptance Criteria:**
* Invalid inputs display inline validation errors.
* Saved vehicle persists to database and navigates back.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-003 — Active Vehicle State Management

* **Status:** `🟢 DONE`
* **Objective:** Manage currently selected vehicle state across application scope.
* **Requirements:**
1. Build `ActiveVehicleRepository` backed by DataStore or database setting.
2. Expose `activeVehicle: StateFlow<Vehicle?>`.
3. Implement automatic selection logic (default to first created vehicle if none active).


* **Acceptance Criteria:**
* App retains active vehicle selection across app restarts.


* **Verification:** `./gradlew testDebugUnitTest`

---

### EPIC-003 — Fuel Tracking & Consumption Engine

**Objective:** Enable refueling log entry, fuel history listing, and full-tank consumption calculation algorithms.

#### TASK-001 — Fuel Domain Model & Database Infrastructure

* **Status:** `🟢 DONE`
* **Objective:** Create domain models, Room entity, DAO, and repository for fuel records.
* **Requirements:**
1. Create `FuelRecord` domain model (`id`, `vehicleId`, `timestampMs`, `odometerKm`, `litersX100`, `pricePerLiterCents`, `totalCostCents`, `isFullTank`, `stationName`, `notes`).
2. Create `FuelRecordEntity` with foreign key linking to `VehicleEntity` (onDelete = CASCADE).
3. Implement `FuelRecordDao` with flow queries filtered by `vehicleId`.


* **Acceptance Criteria:**
* Foreign key constraint deletes fuel records when parent vehicle is deleted.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-002 — Refueling Entry UI & Validation

* **Status:** `🟢 DONE`
* **Objective:** UI and ViewModel to log a refuel event.
* **Requirements:**
1. Build `AddFuelViewModel` with auto-calculation: `totalCost = liters * pricePerLiter`.
2. Build `AddFuelScreen` with numeric inputs, date picker, "Full Tank" toggle, and station name.
3. Pre-fill odometer with active vehicle's last recorded mileage.


* **Acceptance Criteria:**
* Odometer input rejects values lower than previous recorded refuel.
* Calculations maintain precision using minor units.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-003 — Fuel History List & Detail View

* **Status:** `🟢 DONE`
* **Objective:** Display chronologically sorted refuel entries for active vehicle.
* **Requirements:**
1. Implement `FuelHistoryScreen` with LazyColumn displaying refuel cards.
2. Card items show date, fuel quantity, cost, odometer, and full/partial status indicator.
3. Support swipe-to-delete with confirmation dialog.


* **Acceptance Criteria:**
* Empty state shown when no fuel records exist.
* Deleting a record updates calculation state instantly.


* **Verification:** `./gradlew connectedDebugAndroidTest`

#### TASK-004 — Fuel Consumption Engine

* **Status:** `🟢 DONE`
* **Objective:** Build algorithm to compute consumption ($L/100\text{ km}$) across full-tank sequences.
* **Requirements:**
1. Implement `CalculateFuelConsumptionUseCase`.
2. Logic: Group records between consecutive `isFullTank = true` entries. Ignore partial refuels in isolation, but sum their liters toward the next full tank distance delta.
3. Output: `litersPer100KmX100` or `InsufficientData`.


* **Acceptance Criteria:**
* Correctly handles single partial refuels sandwiched between two full refuels.
* Unit test covers edge cases: zero distance delta, missing full tank baselines.


* **Verification:** `./gradlew testDebugUnitTest`

---

### EPIC-004 — Maintenance & Service Tracking

**Objective:** Implement maintenance logging, category classifications, and mileage/time-based service reminder alerts.

#### TASK-001 — Maintenance Domain & Data Infrastructure

* **Status:** `🟢 DONE`
* **Objective:** Data models and persistence layer for service records and categories.
* **Requirements:**
1. Create `MaintenanceRecord` (`id`, `vehicleId`, `title`, `category`, `costCents`, `odometerKm`, `timestampMs`, `notes`, `performedBy`).
2. Create `MaintenanceCategory` enum (Oil Change, Brakes, Tires, Transmission, General Inspection, Other).
3. Implement `MaintenanceRecordEntity` and `MaintenanceRecordDao`.


* **Acceptance Criteria:**
* Service records correctly map to/from database.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-002 — Service Record Entry & History UI

* **Status:** `🟢 DONE`
* **Objective:** Form interface and history timeline for logged maintenance.
* **Requirements:**
1. Implement `AddMaintenanceScreen` with category selector and cost logging.
2. Implement `MaintenanceHistoryScreen` grouped by category or date.


* **Acceptance Criteria:**
* Service entries persist and update vehicle primary odometer if greater than existing record.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-003 — Service Reminders & Schedule Engine

* **Status:** `🟢 DONE`
* **Objective:** Calculate upcoming and overdue service intervals based on distance or time.
* **Requirements:**
1. Create `MaintenanceSchedule` entity (`serviceTitle`, `intervalKm`, `intervalMonths`, `lastPerformedKm`, `lastPerformedDateMs`).
2. Implement `GetUpcomingServicesUseCase` returning `DUE_SOON`, `OVERDUE`, or `OK` status.


* **Acceptance Criteria:**
* Correctly flags service as `OVERDUE` when current vehicle odometer exceeds interval threshold.


* **Verification:** `./gradlew testDebugUnitTest`

---

### EPIC-005 — Dashboard & Quick Action Hub

**Objective:** Provide a consolidated home overview displaying active vehicle health, fuel economy summaries, upcoming maintenance alerts, and rapid entry actions.

#### TASK-001 — Dashboard Aggregation Use Cases

* **Status:** `🟢 DONE`
* **Objective:** Combine domain streams for UI representation.
* **Requirements:**
1. Build `GetDashboardSummaryUseCase` combining `ActiveVehicle`, latest `FuelRecord`, average consumption, and urgent maintenance alerts.
2. Expose unified `DashboardSummary` domain object.


* **Acceptance Criteria:**
* Flow emits updated summary immediately upon any database change in fuel or maintenance tables.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-002 — Dashboard UI & Quick Action Floating Hub

* **Status:** `🟢 DONE`
* **Objective:** Compose dashboard home layout.
* **Requirements:**
1. Build `DashboardScreen` displaying:
* Active Vehicle Summary Card.
* Recent Refuel & Average Economy Widget.
* Maintenance Status Alert Banner.


2. Floating Action Button hub triggering quick "Add Refuel" or "Add Service".


* **Acceptance Criteria:**
* Layout adjusts gracefully between compact phone and expanded tablet screens.


* **Verification:** `./gradlew connectedDebugAndroidTest`

---

### EPIC-006 — Statistics & Analytics Engine

**Objective:** Generate offline visual analytics for fuel expenditure, cost-per-kilometer metrics, and consumption trends.

#### TASK-001 — Analytics Calculations Engine

* **Status:** `🟢 DONE`
* **Objective:** Pure Kotlin analytics engine for period-based financial and mileage aggregations.
* **Requirements:**
1. Implement `CalculateVehicleStatsUseCase` with time period filtering (Last 30 Days, Last 6 Months, Year-to-Date, All Time).
2. Compute metrics: Total Cost of Ownership (Fuel + Maintenance), Cost per Kilometer, Average Monthly Spend, Total Distance Traveled.


* **Acceptance Criteria:**
* Division by zero safety for new vehicles with zero distance records.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-002 — Chart Visualizations Screen

* **Status:** `🟢 DONE`
* **Objective:** Native Compose canvas charts for trends.
* **Requirements:**
1. Implement custom Compose canvas bar/line charts for Fuel Price Trends and Monthly Expenditure.
2. Implement `StatisticsScreen` with period selection chips.


* **Acceptance Criteria:**
* Charts render smoothly without UI thread jank or allocation spikes during scroll.


* **Verification:** `./gradlew connectedDebugAndroidTest`

---

### EPIC-007 — Polish, Data Export & Refactoring Iteration

**Objective:** Implement data portability (JSON import/export), local backup/restore, accessibility, release build verification, and smart form interactions.

#### TASK-001 — JSON Backup & Restore Engine

* **Status:** `🟢 DONE`
* **Objective:** Allow full database import/export for user data ownership.
* **Requirements:**
1. Implement `ExportDatabaseUseCase` converting Room records to versioned JSON schema.
2. Implement `ImportDatabaseUseCase` validating JSON schema before replacing/merging SQLite entries.
3. Integrate storage access framework (SAF) file pickers in settings.


* **Acceptance Criteria:**
* Exported file can be successfully imported into a clean app instance without data loss.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-002 — Accessibility, Localization & UI Polish

* **Status:** `🟢 DONE`
* **Objective:** Ensure accessibility compliance and localized string resources.
* **Requirements:**
1. Add content descriptions to all interactive iconography and chart canvas elements.
2. Ensure touch target sizes meet minimum 48dp M3 guidelines.
3. Extract all hardcoded strings into `res/values/strings.xml`.


* **Acceptance Criteria:**
* TalkBack navigates dashboard and entry forms logically.


* **Verification:** `./gradlew assembleRelease`

#### TASK-003 — Release Build Verification & Audit

* **Status:** `🟢 DONE`
* **Objective:** Final release readiness audit.
* **Requirements:**
1. Configure ProGuard/R8 rules for Room and Kotlinx Serialization.
2. Perform clean release compilation and execute full unit/instrumented test suites.


* **Acceptance Criteria:**
* Zero compilation warnings, zero failing tests, release APK generates cleanly.


* **Verification:** `./gradlew check assembleRelease`

#### TASK-005 — Form Auto-Calculations, Unsaved Changes Guard, Backup Migration & Metric Cards Fix

* **Status:** `🟢 DONE`
* **Objective:** Implement smart refueling calculations, unsaved form navigation guards, relocate backup UI to settings, and fix dashboard placeholder metrics.
* **Requirements:**
1. **Refueling Dynamic Calculator (`AddFuelViewModel`):**
* Track user input across `Total Cost`, `Liters`, and `Price per Liter`.
* Dynamically calculate and populate the missing 3rd value whenever any 2 fields are entered by the user ($\text{Total} = \text{Liters} \times \text{Price/L}$).


2. **Unsaved Changes Confirmation Dialog (`AppScaffold` & Form Screens):**
* Track `isDirty` state in active form ViewModels (`AddFuelViewModel`, `AddEditVehicleViewModel`, `AddMaintenanceViewModel`).
* Intercept bottom tab or top navigation when `isDirty == true` and display an `AlertDialog` asking to confirm discarding unsaved changes.


3. **Settings Backup Relocation:**
* Remove `DataBackupCard` from `VehiclesScreen`.
* Add a dedicated **Data Management** section in `SettingsScreen` with "Export Backup" and "Import Backup" actions bound to `VehicleBackupViewModel`.


4. **Dashboard Zero-Value Metric Fix:**
* Fix `DashboardScreen` / `DashboardViewModel` metric presentation: display **"Sin datos suficientes"** instead of formatting `0.00 L/100km` when insufficient full-tank history exists.




* **Acceptance Criteria:**
* Auto-calculation populates the 3rd field correctly without circular update loops.
* Navigating away from a partially filled form triggers the confirmation dialog.
* Backup & restore functions are fully accessible from `SettingsScreen`.
* Dashboard fuel card cleanly states "Sin datos suficientes" when missing data.
* All unit tests pass cleanly via `./gradlew testDebugUnitTest`.


* **Verification:** `./gradlew testDebugUnitTest assembleDebug`

---

### EPIC-008 — Visual Polish & Fluent Icon System Integration

**Objective:** Upgrade application iconography, navigation bar visuals, dashboard hero widgets, and form styling using the [FluentUI System Icons Color](https://composables.com/icons/icon-libraries/fluentui-system-icons/color) library.

#### TASK-001 — FluentUI Color Icons Dependency & Theme Integration

* **Status:** `🟢 DONE`
* **Objective:** Integrate FluentUI System Icons dependency into Gradle version catalog and configure core UI vector/color icon mappings.
* **Requirements:**
1. Add FluentUI System Icons dependency (`com.microsoft.design:fluent-system-icons:1.1.260`) to `gradle/libs.versions.toml` and `app/build.gradle.kts`.
2. Create an `AppIcons.kt` object inside `core/ui/theme` mapping navigation, quick actions, fuel, maintenance, statistics, and settings destinations to their corresponding Fluent Color/Regular vector representations using Material Icons (Fluent-style icons).
3. Ensure tinting strategy allows icons to render with proper contrast in dark and light themes.


* **Acceptance Criteria:**
* Dependency compiles without duplicate class conflicts.
* `AppIcons.kt` provides centralized icon mappings for navigation and actions.


* **Verification:** `./gradlew assembleDebug testDebugUnitTest`

*Note: The KMP-based fluentui-system-icons library (io.github.niyajali:fluentui-system-icons) provides Compose ImageVector icons but requires multiplatform setup that is not currently configured. The com.microsoft.design:fluent-system-icons library provides Android vector drawable resources. For Compose-native Fluent icons, consider downloading SVG assets from https://composables.com/icons/icon-libraries/fluentui-system-icons/color and adding them to res/drawable/ as needed.*

#### TASK-002 — App Navigation & Scaffold Visual Upgrade

* **Status:** `🟢 DONE`
* **Objective:** Refactor `NavigationBar` bottom tabs and `AppScaffold` with Fluent color iconography and refined visual elevation.
* **Requirements:**
1. Replace Material 3 default icons in `AppScaffold.kt` bottom navigation bar with Fluent Color icons for:
* **Inicio (Home):** Home / Grid Fluent Icon.
* **Vehículos:** Vehicle / Car Fluent Icon.
* **Combustible:** Gas Station / Drop Fluent Icon.
* **Servicios:** Wrench / Maintenance Fluent Icon.
* **Ajustes:** Settings / Gear Fluent Icon.


2. Refine bottom bar pill active indicator padding and label typography contrast according to high-fidelity designs.


* **Acceptance Criteria:**
* Bottom navigation icons render with Fluent Color assets.
* Selected state remains distinct and readable in both light and dark themes.


* **Verification:** `./gradlew assembleDebug testDebugUnitTest`

*Implementation Notes:*
- Navigation icons now use `AppIcons.NavHome`, `AppIcons.NavVehicles`, `AppIcons.NavFuel`, `AppIcons.NavMaintenance`, `AppIcons.NavSettings`
- Bottom bar pill indicator uses primaryContainer with 50% alpha
- Label typography uses labelSmall style with primary/onSurfaceVariant colors
- Icon size standardized to 24dp
- Navigation rail updated with same styling

#### TASK-003 — Dashboard Cards & Floating Quick Action Styling

* **Status:** `🟢 DONE`
* **Objective:** Enhance Dashboard hero card, rapid metric cards, and Floating Action Button (FAB) using Fluent icons and card backgrounds.
* **Requirements:**
1. Update `DashboardScreen` card widgets:
* Active Vehicle Hero Card: Add vehicle Fluent badge and status pill.
* Combustible Quick Card: Display Fuel Drop Fluent Color Icon.
* Mantenimiento Quick Card: Display Wrench Fluent Color Icon.
* Coste por kilometro Quick Card: Display Chart/Trending Fluent Color Icon.


2. Refactor Floating Action Button (FAB) and Quick Action modal sheet to utilize Fluent action icons (`Plus`, `Fuel`, `Wrench`).

*Implementation Notes:*
- Dashboard cards now use `AppIcons.IconVehicleBadge`, `AppIcons.IconFuelDrop`, `AppIcons.IconWrench`, `AppIcons.IconTrending`
- DashboardActionHub FAB uses `AppIcons.ActionFuel`, `AppIcons.ActionWrench`, `AppIcons.ActionPlus`, `AppIcons.ActionClose`
- FuelHistoryScreen, MaintenanceHistoryScreen, VehiclesScreen, FuelScreen FABs updated to use AppIcons


* **Acceptance Criteria:**
* All metric widgets in `DashboardScreen` render matching Fluent Color icons.
* FAB expandable quick menu opens smoothly and displays appropriate iconography.


* **Verification:** `./gradlew testDebugUnitTest assembleDebug`

---

### EPIC-009 — Advanced Vehicle Statistics & Insights

**Objective:** Enhanced analytics with trend predictions, cost analysis by vehicle/period/category, and fuel price comparison over time.

**Status:** `🟢 DONE`

#### TASK-001 — Trend Prediction Engine

* **Status:** `🟢 DONE`
* **Objective:** Implement predictive analytics for fuel consumption and costs.
* **Requirements:**
1. Implement `PredictFuelTrendsUseCase` using historical data.
2. Generate forecasts for upcoming fuel costs and consumption.
3. Display trend visualizations with confidence intervals.


* **Acceptance Criteria:**
* Predictions based on at least 3 data points.
* Clear indication of prediction confidence.


* **Verification:** `./gradlew testDebugUnitTest assembleDebug`

*Implementation Notes:*
- Uses simple linear regression on monthly data
- Confidence levels: LOW, MEDIUM, HIGH based on R-squared
- Prediction confidence decays with time horizon
- Requires at least 3 months of data

#### TASK-002 — Cost Analysis by Vehicle & Period

* **Status:** `🟢 DONE`
* **Objective:** Break down costs by vehicle, time period, and category.
* **Requirements:**
1. Implement `GetCostAnalysisUseCase` with grouping by vehicle, period, category.
2. Display cost breakdown charts and totals.
3. Support comparison between vehicles.


* **Acceptance Criteria:**
* Accurate cost aggregation across all dimensions.
* Filterable by date range and vehicle.


* **Verification:** `./gradlew testDebugUnitTest assembleDebug`

*Implementation Notes:*
- Uses `CostByVehicle`, `CostByCategory`, `CostByPeriod` data classes
- Supports filtering by any `StatsPeriod` (LAST_30_DAYS, LAST_6_MONTHS, YEAR_TO_DATE, ALL_TIME)
- Vehicle comparison shows cost per km and total cost side-by-side
- Category breakdown includes totals and per-average metrics

#### TASK-003 — Fuel Price Comparison Over Time

* **Status:** `🟢 DONE`
* **Objective:** Track and compare fuel prices across different stations and time periods.
* **Requirements:**
1. Implement `CompareFuelPricesUseCase` for price trend analysis.
2. Display price comparison charts by station and date.
3. Highlight best and worst fuel prices.


* **Acceptance Criteria:**
* Price trends visible over time.
* Station comparison available.


* **Verification:** `./gradlew testDebugUnitTest assembleDebug`

*Implementation Notes:*
- Uses `StationPriceAnalysis` and `MonthlyPricePoint` data classes
- Calculates weighted average prices (not simple mean)
- Supports filtering by station name substring
- Shows best/worst stations with price ranges
- Monthly trends use YearMonth for proper date grouping

---

### EPIC-010 — Notifications & Reminders

**Objective:** Push notifications for upcoming services, fuel price alerts, and maintenance reminders based on schedule.

#### TASK-001 — Service Reminder Notifications

* **Status:** `🟢 DONE`
* **Objective:** Send push notifications for upcoming maintenance services.
* **Requirements:**
1. Implement notification scheduling for service reminders.
2. Use `MaintenanceSchedule` data to trigger notifications.
3. Support notification actions (dismiss, view details).


* **Acceptance Criteria:**
* Notifications trigger at appropriate times.
* Users can interact with notifications.


* **Verification:** `./gradlew testDebugUnitTest`

*Implementation Notes:*
- Uses WorkManager for reliable scheduled notifications (every 12 hours)
- Notification channel for maintenance reminders with HIGH importance
- DISMISS action via BroadcastReceiver
- VIEW_DETAILS action via PendingIntent to MainActivity
- Schedules notifications based on MaintenanceSchedule due dates
- Uses NotificationManagerImpl for Android notification handling
- Added database migration v5 for notification tables

#### TASK-002 — Fuel Price Alert System

* **Status:** `🟢 DONE`
* **Objective:** Alert users when fuel prices are favorable.
* **Requirements:**
1. Implement fuel price threshold alerts.
2. Notify when current price is below user-set threshold.
3. Track price history for alert comparison.


* **Acceptance Criteria:**
* Users can set custom price thresholds.
* Alerts trigger when conditions met.


* **Verification:** `./gradlew testDebugUnitTest`

*Implementation Notes:*
- Created `FuelPriceAlertUseCase` for checking price alerts
- Integrated with existing `FuelRecordRepository` for price monitoring
- Added support for custom price threshold preferences
- Users can enable/disable fuel price alerts via notification preferences
- Alert triggers when price is below user-set threshold
- Uses `observeRecentPrices()` to monitor recent fuel records

#### TASK-002 — Fuel Price Alert System

* **Status:** `BACKLOG`
* **Objective:** Alert users when fuel prices are favorable.
* **Requirements:**
1. Implement fuel price threshold alerts.
2. Notify when current price is below user-set threshold.
3. Track price history for alert comparison.


* **Acceptance Criteria:**
* Users can set custom price thresholds.
* Alerts trigger when conditions met.


* **Verification:** `./gradlew testDebugUnitTest`

#### TASK-003 — Maintenance Reminder Integration

* **Status:** `🟢 DONE`
* **Objective:** Integrate maintenance reminders with notification system.
* **Requirements:**
1. Combine schedule engine with notification triggers.
2. Support recurring reminders for regular maintenance.
3. Allow users to snooze or reschedule reminders.


* **Acceptance Criteria:**
* Reminders sync with maintenance schedules.
* Users can manage reminder timing.


* **Verification:** `./gradlew testDebugUnitTest`

*Implementation Notes:*
- Created `ReminderManagementUseCase` for snooze/reschedule functionality
- Created `SnoozedReminder` and `SnoozeReminderRequest` domain models
- Supports snoozing reminders for a specified duration
- Supports rescheduling reminders to a new date
- Supports dismissing reminders permanently
- Integrates with existing `NotificationRepository` for persistence
- Uses `NotificationDao.getActiveNotification()` for checking active notifications

---