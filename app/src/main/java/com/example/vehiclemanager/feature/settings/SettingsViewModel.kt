package com.example.vehiclemanager.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ReminderPreferences
import com.example.vehiclemanager.core.domain.ReminderPreferencesRepository
import com.example.vehiclemanager.core.domain.ThemePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferenceRepository: ThemePreferenceRepository,
    private val reminderPreferencesRepository: ReminderPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        themePreferenceRepository.isDarkTheme,
        reminderPreferencesRepository.reminderPreferences,
    ) { isDarkTheme, reminderPrefs ->
        SettingsUiState(
            isDarkTheme = isDarkTheme,
            reminderPreferences = reminderPrefs,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SettingsUiState(),
    )

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            themePreferenceRepository.setDarkTheme(enabled)
        }
    }

    // Reminder preferences
    fun setAdvanceDistanceKm(km: Long) {
        viewModelScope.launch {
            reminderPreferencesRepository.setAdvanceDistanceKm(km)
        }
    }

    fun setAdvanceDays(days: Int) {
        viewModelScope.launch {
            reminderPreferencesRepository.setAdvanceDays(days)
        }
    }

    fun setNotificationTime(hours: Int, minutes: Int) {
        viewModelScope.launch {
            reminderPreferencesRepository.setNotificationTime(hours, minutes)
        }
    }

    fun setFuelNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            reminderPreferencesRepository.setFuelNotificationsEnabled(enabled)
        }
    }

    fun setTirePressureNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            reminderPreferencesRepository.setTirePressureNotificationsEnabled(enabled)
        }
    }

    fun setVibrateOnNotification(enabled: Boolean) {
        viewModelScope.launch {
            reminderPreferencesRepository.setVibrateOnNotification(enabled)
        }
    }
}

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val reminderPreferences: ReminderPreferences = ReminderPreferences(),
)
