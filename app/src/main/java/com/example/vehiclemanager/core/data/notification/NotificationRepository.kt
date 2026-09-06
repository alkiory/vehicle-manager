package com.example.vehiclemanager.core.data.notification

import com.example.vehiclemanager.core.domain.NotificationPreferences
import com.example.vehiclemanager.core.domain.NotificationSchedule
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notification operations.
 */
interface NotificationRepository {
    fun observeAllNotifications(): Flow<List<NotificationScheduleEntity>>
    fun observeNotificationsForVehicle(vehicleId: Long): Flow<List<NotificationScheduleEntity>>
    suspend fun getNotification(id: Long): NotificationScheduleEntity?
    suspend fun insertNotification(notification: NotificationScheduleEntity): Long
    suspend fun updateNotification(notification: NotificationScheduleEntity)
    suspend fun dismissNotification(id: Long)
    suspend fun markAsTriggered(id: Long, triggeredDateMs: Long)
    suspend fun deleteNotification(id: Long)

    suspend fun getPreferences(): NotificationPreferences?
    suspend fun updatePreferences(preferences: NotificationPreferences)
}
