package com.example.vehiclemanager.core.data.notification

import com.example.vehiclemanager.core.domain.NotificationPreferences
import com.example.vehiclemanager.core.domain.NotificationSchedule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationRepository using Room database.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
) : NotificationRepository {

    override fun observeAllNotifications(): Flow<List<NotificationScheduleEntity>> {
        return notificationDao.observeAllNotifications()
    }

    override fun observeNotificationsForVehicle(vehicleId: Long): Flow<List<NotificationScheduleEntity>> {
        return notificationDao.observeNotificationsForVehicle(vehicleId)
    }

    override suspend fun getNotification(id: Long): NotificationScheduleEntity? {
        return notificationDao.getNotification(id)
    }

    override suspend fun insertNotification(notification: NotificationScheduleEntity): Long {
        return notificationDao.insertNotification(notification)
    }

    override suspend fun updateNotification(notification: NotificationScheduleEntity) {
        notificationDao.updateNotification(notification)
    }

    override suspend fun dismissNotification(id: Long) {
        notificationDao.dismissNotification(id)
    }

    override suspend fun markAsTriggered(id: Long, triggeredDateMs: Long) {
        notificationDao.markAsTriggered(id, triggeredDateMs)
    }

    override suspend fun deleteNotification(id: Long) {
        notificationDao.deleteNotification(id)
    }

    override suspend fun getPreferences(): NotificationPreferences? {
        return notificationDao.getPreferences()?.toDomain()
    }

    override suspend fun updatePreferences(preferences: NotificationPreferences) {
        // Ensure preferences exist first
        if (!notificationDao.hasPreferences()) {
            notificationDao.initializeDefaultPreferences()
        }
        notificationDao.updatePreferences(preferences.toEntity())
    }

    suspend fun initializeDefaultPreferences() {
        if (!notificationDao.hasPreferences()) {
            notificationDao.initializeDefaultPreferences()
        }
    }
}

// Extension functions for mapping between entity and domain
private fun NotificationScheduleEntity.toDomain(): NotificationSchedule {
    return NotificationSchedule(
        id = id,
        vehicleId = vehicleId,
        serviceTitle = serviceTitle,
        scheduledDateMs = scheduledDateMs,
        isDismissed = isDismissed,
        isTriggered = isTriggered,
        triggeredDateMs = triggeredDateMs,
        maintenanceScheduleId = maintenanceScheduleId,
    )
}

private fun NotificationPreferencesEntity.toDomain(): NotificationPreferences {
    return NotificationPreferences(
        maintenanceRemindersEnabled = maintenanceRemindersEnabled == 1,
        fuelPriceAlertsEnabled = fuelPriceAlertsEnabled == 1,
        reminderDaysBeforeDue = reminderDaysBeforeDue,
        fuelPriceAlertThreshold = fuelPriceAlertThreshold,
    )
}

private fun NotificationPreferences.toEntity(): NotificationPreferencesEntity {
    return NotificationPreferencesEntity(
        id = 1,
        maintenanceRemindersEnabled = if (maintenanceRemindersEnabled) 1 else 0,
        fuelPriceAlertsEnabled = if (fuelPriceAlertsEnabled) 1 else 0,
        reminderDaysBeforeDue = reminderDaysBeforeDue,
        fuelPriceAlertThreshold = fuelPriceAlertThreshold,
    )
}
