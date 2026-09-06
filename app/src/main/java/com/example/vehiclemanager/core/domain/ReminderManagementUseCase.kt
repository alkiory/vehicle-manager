package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Represents a snoozed reminder that has been postponed.
 */
data class SnoozedReminder(
    val id: Long = 0,
    val originalScheduleId: Long,
    val vehicleId: Long,
    val serviceTitle: String,
    val originalDueDateMs: Long,
    val snoozeUntilDateMs: Long,
    val snoozeReason: String? = null,
    val isActive: Boolean = true,
    val createdAtMs: Long = System.currentTimeMillis(),
)

/**
 * Request to snooze a reminder.
 */
data class SnoozeReminderRequest(
    val scheduleId: Long,
    val vehicleId: Long,
    val serviceTitle: String,
    val originalDueDateMs: Long,
    val snoozeDurationMs: Long, // How long to snooze (e.g., 7 days = 7 * 24 * 60 * 60 * 1000)
    val reason: String? = null,
)

/**
 * Reminder management use case for snoozing and rescheduling.
 */
class ReminderManagementUseCase(
    private val notificationRepository: com.example.vehiclemanager.core.data.notification.NotificationRepository,
) {
    /**
     * Snooze a reminder for a specified duration.
     */
    suspend fun snoozeReminder(request: SnoozeReminderRequest): Long {
        // Save to notification repository as a notification schedule with snoozed date
        val entity = com.example.vehiclemanager.core.data.notification.NotificationScheduleEntity(
            id = request.scheduleId,
            vehicleId = request.vehicleId,
            serviceTitle = request.serviceTitle,
            scheduledDateMs = request.originalDueDateMs + request.snoozeDurationMs,
            isDismissed = false,
            isTriggered = false,
            maintenanceScheduleId = request.scheduleId,
        )
        
        return notificationRepository.insertNotification(entity)
    }

    /**
     * Get all active snoozed reminders.
     */
    fun getActiveSnoozedReminders(): Flow<List<SnoozedReminder>> {
        return notificationRepository.observeAllNotifications().map { entities ->
            entities
                .filter { it.isTriggered == false && it.isDismissed == false }
                .map { entity ->
                    SnoozedReminder(
                        id = entity.id,
                        originalScheduleId = entity.maintenanceScheduleId ?: 0,
                        vehicleId = entity.vehicleId,
                        serviceTitle = entity.serviceTitle,
                        originalDueDateMs = entity.scheduledDateMs,
                        snoozeUntilDateMs = entity.scheduledDateMs,
                        isActive = true,
                    )
                }
        }
    }

    /**
     * Cancel a snoozed reminder (delete it).
     */
    suspend fun cancelSnoozedReminder(snoozeId: Long) {
        notificationRepository.deleteNotification(snoozeId)
    }

    /**
     * Reschedule a reminder to a new date.
     */
    suspend fun rescheduleReminder(
        scheduleId: Long,
        newDueDateMs: Long,
        vehicleId: Long,
        serviceTitle: String,
    ) {
        // Update the existing notification or create a new one
        val existingNotification = notificationRepository.getNotification(scheduleId)
        val entity = com.example.vehiclemanager.core.data.notification.NotificationScheduleEntity(
            id = scheduleId,
            vehicleId = vehicleId,
            serviceTitle = serviceTitle,
            scheduledDateMs = newDueDateMs,
            isDismissed = existingNotification?.isDismissed ?: false,
            isTriggered = false,
            maintenanceScheduleId = scheduleId,
        )
        
        notificationRepository.insertNotification(entity)
    }

    /**
     * Dismiss a reminder permanently.
     */
    suspend fun dismissReminder(scheduleId: Long) {
        notificationRepository.dismissNotification(scheduleId)
    }
}
