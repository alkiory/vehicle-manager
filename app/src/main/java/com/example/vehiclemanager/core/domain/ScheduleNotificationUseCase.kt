package com.example.vehiclemanager.core.domain

/**
 * Use case interface for scheduling notifications.
 * Implementation will be provided by the data layer with Android-specific notification handling.
 */
interface ScheduleNotificationUseCase {
    /**
     * Schedule a notification for a maintenance service.
     */
    suspend fun scheduleServiceReminder(request: ScheduleNotificationRequest): Long

    /**
     * Cancel a scheduled notification.
     */
    suspend fun cancelNotification(notificationId: Long)

    /**
     * Dismiss a notification (mark as dismissed without necessarily cancelling the scheduled job).
     */
    suspend fun dismissNotification(notificationId: Long)

    /**
     * Get all scheduled notifications for a vehicle.
     */
    fun getScheduledNotifications(vehicleId: Long): kotlinx.coroutines.flow.Flow<List<NotificationSchedule>>

    /**
     * Update notification preferences.
     */
    suspend fun updatePreferences(preferences: NotificationPreferences)
}
