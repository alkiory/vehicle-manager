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

## Conventions established in EPIC-008 / TASK-001

- **FluentUI Icons:** The FluentUI System Icons library (`com.microsoft.design:fluent-system-icons:1.1.260`) is added to the version catalog for Android vector drawable resources.
- **AppIcons.kt:** Centralized icon mappings live in `core/ui/theme/AppIcons.kt`, providing:
  - `AppIcons.NavHome`, `AppIcons.NavVehicles`, `AppIcons.NavFuel`, `AppIcons.NavMaintenance`, `AppIcons.NavSettings` for navigation
  - `AppIcons.ActionPlus`, `AppIcons.ActionClose`, `AppIcons.ActionFuel`, `AppIcons.ActionWrench`, `AppIcons.ActionChart` for FAB/actions
  - `AppIcons.IconFuelDrop`, `AppIcons.IconWrench`, `AppIcons.IconTrending`, `AppIcons.IconVehicleBadge` for feature widgets
  - `NavIcon` and `ActionIcon` enums for type-safe icon references
- **Icon Selection:** Material Icons Extended is used as the primary Compose icon source; it provides similar icon coverage to Fluent icons. For true Fluent Color icons, download SVG/vector assets from https://composables.com/icons/icon-libraries/fluentui-system-icons/color.
- **Tinting:** Single-tone icons are tinted with the theme's current color scheme (primary, secondary, tertiary). Multi-tone colored icons (when added later) should use `Color.Unspecified` tint to preserve their native colors.
- **Composition:** All icons should be rendered through `AppIcons.Icon()` composable for consistent tinting and content description handling.

## Conventions established in EPIC-008 / TASK-002

- **Navigation Bar Styling:** Bottom navigation bar uses:
  - Primary color for selected icon and label, onSurfaceVariant for unselected
  - `labelSmall` typography for labels
  - 24dp icon size
  - Pill indicator with `primaryContainer.copy(alpha = 0.5f)` for softer visual appearance
- **Icon Consistency:** All navigation icons should be accessed through `AppIcons` object, not directly from Material Icons
- **Navigation Rail:** Uses same icon and color conventions as bottom navigation bar
- **Content Descriptions:** Each navigation item has proper contentDescription for accessibility

## Upcoming Epics

### EPIC-011 — (TBD)
- Future enhancements can be added here

---

## EPIC-010 — Notifications & Reminders (Complete)

### Notification Architecture
- **NotificationManagerImpl**: Android notification handling with channels
- **ServiceReminderWorker**: WorkManager worker for periodic checks (every 12 hours)
- **MaintenanceNotificationReceiver**: Broadcast receiver for dismiss actions
- **NotificationRepository**: Room-backed persistence for notification state

### Notification Domain Models
- `NotificationSchedule`: Scheduled notification entity
- `NotificationPreferences`: User notification settings
- `DueServiceNotification`: Due/upcoming service detection
- `FuelPriceAlert`: Fuel price threshold alert
- `SnoozedReminder`: Snoozed reminder tracking

### Integration Points
- `MaintenanceScheduleRepository.observeSchedulesForActiveVehicle()` - Active vehicle schedules
- `FuelRecordRepository.observeRecentPrices()` - Recent price monitoring
- `GetDueServicesUseCase` - Due service detection
- `FuelPriceAlertUseCase` - Price alert detection
- `ReminderManagementUseCase` - Snooze/reschedule management

### Database
- Migration v5 added notification tables
- Notification channels: maintenance_reminders (HIGH), fuel_price_alerts (DEFAULT)

### Permissions Required
- POST_NOTIFICATIONS (Android 13+)
- RECEIVE_BOOT_COMPLETED (for scheduling after reboot)
