package com.example.vehiclemanager.core.domain

/**
 * Represents a scheduled notification for a maintenance service.
 */
data class NotificationSchedule(
    val id: Long = 0,
    val vehicleId: Long,
    val serviceTitle: String,
    val scheduledDateMs: Long,
    val isDismissed: Boolean = false,
    val isTriggered: Boolean = false,
    val triggeredDateMs: Long? = null,
    val maintenanceScheduleId: Long? = null,
)

/**
 * Request to schedule a notification for a service.
 */
data class ScheduleNotificationRequest(
    val vehicleId: Long,
    val serviceTitle: String,
    val scheduledDateMs: Long,
    val maintenanceScheduleId: Long? = null,
)

/**
 * Notification action types.
 */
enum class NotificationAction {
    DISMISS,
    VIEW_DETAILS,
}

/**
 * Notification preference settings.
 */
data class NotificationPreferences(
    val maintenanceRemindersEnabled: Boolean = true,
    val fuelPriceAlertsEnabled: Boolean = true,
    val reminderDaysBeforeDue: Int = 7,
    val fuelPriceAlertThreshold: Long = 0, // price per liter cents
)
