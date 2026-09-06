package com.example.vehiclemanager.core.data.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for notification schedule operations.
 */
@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification_schedules ORDER BY scheduledDateMs ASC")
    fun observeAllNotifications(): Flow<List<NotificationScheduleEntity>>

    @Query("SELECT * FROM notification_schedules WHERE vehicleId = :vehicleId ORDER BY scheduledDateMs ASC")
    fun observeNotificationsForVehicle(vehicleId: Long): Flow<List<NotificationScheduleEntity>>

    @Query("SELECT * FROM notification_schedules WHERE id = :id AND isTriggered = 0 AND isDismissed = 0")
    suspend fun getActiveNotification(id: Long): NotificationScheduleEntity?

    @Query("SELECT * FROM notification_schedules WHERE id = :id")
    suspend fun getNotification(id: Long): NotificationScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationScheduleEntity): Long

    @Update
    suspend fun updateNotification(notification: NotificationScheduleEntity)

    @Query("UPDATE notification_schedules SET isDismissed = 1 WHERE id = :id")
    suspend fun dismissNotification(id: Long)

    @Query("UPDATE notification_schedules SET isTriggered = 1, triggeredDateMs = :triggeredDateMs WHERE id = :id")
    suspend fun markAsTriggered(id: Long, triggeredDateMs: Long)

    @Query("DELETE FROM notification_schedules WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    // Preferences queries
    @Query("SELECT * FROM notification_preferences WHERE id = 1")
    suspend fun getPreferences(): NotificationPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: NotificationPreferencesEntity)

    @Update
    suspend fun updatePreferences(preferences: NotificationPreferencesEntity)

    @Query("SELECT COUNT(*) > 0 FROM notification_preferences")
    suspend fun hasPreferences(): Boolean

    @Query("INSERT OR REPLACE INTO notification_preferences (id, maintenanceRemindersEnabled, fuelPriceAlertsEnabled, reminderDaysBeforeDue, fuelPriceAlertThreshold) VALUES (1, :maintenanceEnabled, :fuelEnabled, :days, :threshold)")
    suspend fun initializeDefaultPreferences(
        maintenanceEnabled: Int = 1,
        fuelEnabled: Int = 1,
        days: Int = 7,
        threshold: Long = 0,
    )
}
