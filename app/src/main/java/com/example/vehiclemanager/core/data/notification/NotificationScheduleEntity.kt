package com.example.vehiclemanager.core.data.notification

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing notification schedules.
 */
@Entity(tableName = "notification_schedules")
data class NotificationScheduleEntity(
    @PrimaryKey(autoGenerate = true)
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
 * Room entity for storing notification preferences.
 */
@Entity(tableName = "notification_preferences")
data class NotificationPreferencesEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for preferences
    val maintenanceRemindersEnabled: Int = 1, // Store as Int (0 or 1)
    val fuelPriceAlertsEnabled: Int = 1,
    val reminderDaysBeforeDue: Int = 7,
    val fuelPriceAlertThreshold: Long = 0,
)
