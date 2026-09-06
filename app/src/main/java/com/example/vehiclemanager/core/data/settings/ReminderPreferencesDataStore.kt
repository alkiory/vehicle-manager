package com.example.vehiclemanager.core.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.vehiclemanager.core.domain.ReminderPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.reminderPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "reminder_preferences",
)

@Singleton
class ReminderPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object PreferencesKeys {
        val ADVANCE_DISTANCE_KM = longPreferencesKey("advance_distance_km")
        val ADVANCE_DAYS = intPreferencesKey("advance_days")
        val NOTIFICATION_TIME_HOURS = intPreferencesKey("notification_time_hours")
        val NOTIFICATION_TIME_MINUTES = intPreferencesKey("notification_time_minutes")
        val FUEL_NOTIFICATIONS_ENABLED = booleanPreferencesKey("fuel_notifications_enabled")
        val TIRE_PRESSURE_NOTIFICATIONS_ENABLED = booleanPreferencesKey("tire_pressure_notifications_enabled")
        val VIBRATE_ON_NOTIFICATION = booleanPreferencesKey("vibrate_on_notification")
    }

    val reminderPreferences: Flow<ReminderPreferences> = context.reminderPreferencesDataStore.data.map { preferences ->
        ReminderPreferences(
            advanceDistanceKm = preferences[PreferencesKeys.ADVANCE_DISTANCE_KM] ?: 500L,
            advanceDays = preferences[PreferencesKeys.ADVANCE_DAYS] ?: 7,
            notificationTimeHours = preferences[PreferencesKeys.NOTIFICATION_TIME_HOURS] ?: 9,
            notificationTimeMinutes = preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTES] ?: 0,
            fuelNotificationsEnabled = preferences[PreferencesKeys.FUEL_NOTIFICATIONS_ENABLED] ?: true,
            tirePressureNotificationsEnabled = preferences[PreferencesKeys.TIRE_PRESSURE_NOTIFICATIONS_ENABLED] ?: false,
            vibrateOnNotification = preferences[PreferencesKeys.VIBRATE_ON_NOTIFICATION] ?: true,
        )
    }

    suspend fun updateAdvanceDistanceKm(km: Long) {
        context.reminderPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.ADVANCE_DISTANCE_KM] = km
        }
    }

    suspend fun updateAdvanceDays(days: Int) {
        context.reminderPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.ADVANCE_DAYS] = days
        }
    }

    suspend fun updateNotificationTime(hours: Int, minutes: Int) {
        context.reminderPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIME_HOURS] = hours
            preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTES] = minutes
        }
    }

    suspend fun updateFuelNotificationsEnabled(enabled: Boolean) {
        context.reminderPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.FUEL_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateTirePressureNotificationsEnabled(enabled: Boolean) {
        context.reminderPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.TIRE_PRESSURE_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateVibrateOnNotification(enabled: Boolean) {
        context.reminderPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATE_ON_NOTIFICATION] = enabled
        }
    }
}
