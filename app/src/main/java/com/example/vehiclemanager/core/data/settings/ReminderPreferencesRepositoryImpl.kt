package com.example.vehiclemanager.core.data.settings

import com.example.vehiclemanager.core.domain.ReminderPreferences
import com.example.vehiclemanager.core.domain.ReminderPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: ReminderPreferencesDataStore,
) : ReminderPreferencesRepository {

    override val reminderPreferences: Flow<ReminderPreferences> = dataStore.reminderPreferences

    override suspend fun setAdvanceDistanceKm(km: Long) {
        dataStore.updateAdvanceDistanceKm(km)
    }

    override suspend fun setAdvanceDays(days: Int) {
        dataStore.updateAdvanceDays(days)
    }

    override suspend fun setNotificationTime(hours: Int, minutes: Int) {
        dataStore.updateNotificationTime(hours, minutes)
    }

    override suspend fun setFuelNotificationsEnabled(enabled: Boolean) {
        dataStore.updateFuelNotificationsEnabled(enabled)
    }

    override suspend fun setTirePressureNotificationsEnabled(enabled: Boolean) {
        dataStore.updateTirePressureNotificationsEnabled(enabled)
    }

    override suspend fun setVibrateOnNotification(enabled: Boolean) {
        dataStore.updateVibrateOnNotification(enabled)
    }

    override suspend fun updateAll(preferences: ReminderPreferences) {
        dataStore.updateAdvanceDistanceKm(preferences.advanceDistanceKm)
        dataStore.updateAdvanceDays(preferences.advanceDays)
        dataStore.updateNotificationTime(preferences.notificationTimeHours, preferences.notificationTimeMinutes)
        dataStore.updateFuelNotificationsEnabled(preferences.fuelNotificationsEnabled)
        dataStore.updateTirePressureNotificationsEnabled(preferences.tirePressureNotificationsEnabled)
        dataStore.updateVibrateOnNotification(preferences.vibrateOnNotification)
    }
}
