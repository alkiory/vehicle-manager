# Vehicle Manager — Master Product & Architecture Blueprint

This master specification document establishes the technical blueprint, architectural conventions, agent protocol, and detailed task breakdowns for building the **Vehicle Manager** native Android application.

---

## 1. System Architecture & Technical Stack

### Core Technology Stack

* **Language:** Kotlin 2.0+ (with Compose Compiler Gradle Plugin)
* **Target / Compile SDK:** 35 | **Min SDK:** 26 (Android 8.0)
* **UI Framework:** Jetpack Compose with Material Design 3
* **Iconography & Animation:** FluentUI System Icons & AndroidX Splash Screen API
* **Dependency Injection:** Hilt (`com.google.dagger:hilt-android`)
* **Local Persistence:** Room Database with KSP (`androidx.room`) & Preferences DataStore
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

3. **Offline-First & Local Persistence Guarantee:**
   * Local SQLite database via Room and Android DataStore act as the canonical, permanent source of truth on the user's phone storage.
   * Zero remote network or backend dependencies. Data must survive app kills, process death, and full device reboots.

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
├── EPIC-008-fluent-icons-visual-enhancements/
├── EPIC-009-advanced-statistics/
├── EPIC-010-notifications-reminders/
├── EPIC-011-enhanced-notification-settings/
├── EPIC-012-app-icon-splash-animation/
└── EPIC-013-offline-persistence-and-device-lifecycle/

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
* **TASK-001 — Project Foundation & Dependency Configuration:** `🟢 DONE`
* **TASK-002 — Base Design System & Material 3 Theme:** `🟢 DONE`
* **TASK-003 — Room Base Infrastructure & Converters:** `🟢 DONE`
* **TASK-004 — Navigation Skeleton & Main Scaffold:** `🟢 DONE`

---

### EPIC-002 — Vehicle Management
* **TASK-001 — Vehicle Domain Model & Data Layer:** `🟢 DONE`
* **TASK-002 — Vehicle Entry & Editing Flow:** `🟢 DONE`
* **TASK-003 — Active Vehicle State Management:** `🟢 DONE`

---

### EPIC-003 — Fuel Tracking & Consumption Engine
* **TASK-001 — Fuel Domain Model & Database Infrastructure:** `🟢 DONE`
* **TASK-002 — Refueling Entry UI & Validation:** `🟢 DONE`
* **TASK-003 — Fuel History List & Detail View:** `🟢 DONE`
* **TASK-004 — Fuel Consumption Engine:** `🟢 DONE`

---

### EPIC-004 — Maintenance & Service Tracking
* **TASK-001 — Maintenance Domain & Data Infrastructure:** `🟢 DONE`
* **TASK-002 — Service Record Entry & History UI:** `🟢 DONE`
* **TASK-003 — Service Reminders & Schedule Engine:** `🟢 DONE`

---

### EPIC-005 — Dashboard & Quick Action Hub
* **TASK-001 — Dashboard Aggregation Use Cases:** `🟢 DONE`
* **TASK-002 — Dashboard UI & Quick Action Floating Hub:** `🟢 DONE`

---

### EPIC-006 — Statistics & Analytics Engine
* **TASK-001 — Analytics Calculations Engine:** `🟢 DONE`
* **TASK-002 — Chart Visualizations Screen:** `🟢 DONE`

---

### EPIC-007 — Polish, Data Export & Refactoring Iteration
* **TASK-001 — JSON Backup & Restore Engine:** `🟢 DONE`
* **TASK-002 — Accessibility, Localization & UI Polish:** `🟢 DONE`
* **TASK-003 — Release Build Verification & Audit:** `🟢 DONE`
* **TASK-005 — Form Auto-Calculations, Unsaved Changes Guard, Backup Migration & Metric Cards Fix:** `🟢 DONE`

---

### EPIC-008 — Visual Polish & Fluent Icon System Integration
* **TASK-001 — FluentUI Color Icons Dependency & Theme Integration:** `🟢 DONE`
* **TASK-002 — App Navigation & Scaffold Visual Upgrade:** `🟢 DONE`
* **TASK-003 — Dashboard Cards & Floating Quick Action Styling:** `🟢 DONE`

---

### EPIC-009 — Advanced Vehicle Statistics & Insights
* **TASK-001 — Trend Prediction Engine:** `🟢 DONE`
* **TASK-002 — Cost Analysis by Vehicle & Period:** `🟢 DONE`
* **TASK-003 — Fuel Price Comparison Over Time:** `🟢 DONE`

---

### EPIC-010 — Notifications & Reminders
* **TASK-001 — Service Reminder Notifications:** `🟢 DONE`
* **TASK-002 — Fuel Price Alert System:** `🟢 DONE`
* **TASK-003 — Maintenance Reminder Integration:** `🟢 DONE`

---

### EPIC-011 — Enhanced Notification Settings

**Objective:** Allow user configuration of notification advance thresholds (km/days), preferred alert execution time, and toggle switches for specific alerts.

#### TASK-001 — Reminder Preferences UI & Data Layer
* **Status:** `🟢 DONE`
* **Objective:** Create domain model, DataStore persistence layer, Hilt bindings, and Settings UI section for reminder settings.
* **Requirements:**
  1. Implemented `ReminderPreferences` domain model (`advanceDistanceKm`, `advanceDays`, `notificationTimeHours`, `notificationTimeMinutes`, `fuelNotificationsEnabled`, `tirePressureNotificationsEnabled`, `vibrateOnNotification`).
  2. Implemented `ReminderPreferencesDataStore` and `ReminderPreferencesRepositoryImpl` bound via `ReminderDataModule`.
  3. Integrated "Recordatorios" section in `SettingsScreen` with numerical inputs for km/days, time display, and feature toggles.
* **Acceptance Criteria:**
  * Settings persist across app restarts via DataStore.
* **Verification:** `./gradlew assembleDebug testDebugUnitTest`

---

### EPIC-012 — Custom Application Icon & Animated Startup Screen

**Objective:** Design and implement a branded adaptive launcher icon and a modern, animated splash screen using the official AndroidX SplashScreen API and Compose animations.

#### TASK-001 — Adaptive App Launcher Icon Design
* **Status:** `🟢 DONE`
* **Objective:** Create vector-based adaptive launcher icon resources (`mipmap-hdpi`, `mipmap-xhdpi`, etc.) combining vehicle and fuel/service iconography with the app's visual identity.
* **Requirements:**
  1. Generated SVG/XML vector drawables for launcher icon foreground (`ic_launcher_foreground.xml`) and background (`ic_launcher_background.xml`).
  2. Implemented adaptive icon definitions (`res/mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`) with proper safe-zone margins.
  3. Updated `AndroidManifest.xml` to reference the new adaptive icons.
* **Acceptance Criteria:**
  * Launcher icon scales correctly across all density buckets without clipping in round, squircle, or teardrop launcher masks.
* **Verification:** `./gradlew assembleDebug`

*Implementation Notes:*
- Created vector drawables for ic_launcher_foreground.xml (vehicle silhouette + fuel drop iconography on blue circle)
- Created ic_launcher_background.xml (dark navy background)
- Created adaptive icon definitions in mipmap-anydpi-v26/
- Created fallback vector icons for all density buckets (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi, nodpi)
- Added Theme.SplashScreen configuration in themes.xml
- Added splash_background color
- Updated AndroidManifest.xml with android:icon, android:roundIcon, and splash screen theme
- Icon design combines vehicle + fuel drop for transportation/fuel theme

#### TASK-002 — Animated Startup Splash Screen Integration
* **Status:** `🟢 DONE`
* **Objective:** Implement the official `androidx.core:core-splashscreen` API with an animated branding transition into the main scaffold.
* **Requirements:**
  1. Add `androidx.core:core-splashscreen` dependency to `libs.versions.toml` and `app/build.gradle.kts`.
  2. Create `Theme.App.Starting` splash theme in `res/values/themes.xml` inheriting from `Theme.SplashScreen`.
  3. Call `installSplashScreen()` in `MainActivity.onCreate()` before `setContent`.
  4. Implement an animated entrance transition in Compose (brand icon scale/fade animation) that holds until `ActiveVehicleRepository` and initial Room state are hydrated.
* **Acceptance Criteria:**
  * Cold start displays smooth splash animation with zero white screen flash.
  * Transitions seamlessly into `AppScaffold` once initial data loading is ready.
* **Verification:** `./gradlew assembleDebug testDebugUnitTest`

*Implementation Notes:*
- Added `SplashScreenAnimation` composable with scale and fade animations
- Icon scales from 0 to 1 with FastOutSlowInEasing over 800ms
- Icon fades in with 200ms delay over 600ms
- Splash screen automatically dismissed after animation completes
- `installSplashScreen()` called before `setContent()` in MainActivity
- Theme.App.Starting configured in themes.xml with proper icon and background
- Uses ic_launcher_foreground drawable for splash icon

---

### EPIC-013 — Offline Local Storage & Device Lifecycle Persistence Verification

**Objective:** Ensure complete data persistence, integrity, and seamless restoration across device cold boots, process kills, system reboots, and offline operations without any backend server.

#### TASK-001 — Room DB & DataStore Cold Boot Persistence Audit
* **Status:** `BACKLOG`
* **Objective:** Verify and guarantee that all SQLite database tables (vehicles, fuel records, maintenance schedules, notifications) and DataStore files survive full process terminations and system reboots.
* **Requirements:**
  1. Audit `VehicleDatabase` configuration: ensure write-ahead logging (WAL) is enabled for crash resistance.
  2. Ensure `BOOT_COMPLETED` broadcast receiver (`RECEIVE_BOOT_COMPLETED`) correctly reschedules `WorkManager` notification jobs (`ServiceReminderWorker`) when the phone is turned back on.
  3. Write Android instrumented tests (`androidTest`) simulating app process kill and verifying full state recovery.
* **Acceptance Criteria:**
  * App retains 100% of vehicles, refuel logs, maintenance records, and settings after process kill and device reboot simulations.
  * WorkManager automatically reschedules background reminder tasks upon boot.
* **Verification:** `./gradlew connectedDebugAndroidTest`

#### TASK-002 — Database Schema Auto-Migration & Schema Safety
* **Status:** `BACKLOG`
* **Objective:** Configure Room schema export and automated migration strategy to prevent data loss during future app updates.
* **Requirements:**
  1. Verify `room.schemaLocation` generates schema JSON snapshots for all database versions.
  2. Implement explicit migration rules (`Migration(x, y)`) or fallback-to-destructive-migration guards to protect local user data during updates.
  3. Create an automated test (`MigrationTest`) verifying database migration integrity without corrupting existing records.
* **Acceptance Criteria:**
  * Database upgrades preserve existing local user records without SQLite schema mismatch crashes.
* **Verification:** `./gradlew testDebugUnitTest`

---

### EPIC-014 — (TBD)
- Future enhancements can be added here