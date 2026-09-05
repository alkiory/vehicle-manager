package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.StateFlow

interface ThemePreferenceRepository {
    val isDarkTheme: StateFlow<Boolean>
    suspend fun setDarkTheme(enabled: Boolean)
}
