package com.example.vehiclemanager.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ThemePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferenceRepository: ThemePreferenceRepository,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = themePreferenceRepository.isDarkTheme
        .map { isDark ->
            SettingsUiState(isDarkTheme = isDark)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SettingsUiState(),
        )

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            themePreferenceRepository.setDarkTheme(enabled)
        }
    }
}

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
)
