package com.example.vehiclemanager.core.data.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.vehiclemanager.core.domain.ThemePreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.themeDataStore by preferencesDataStore(
    name = "theme_preferences",
)

@Singleton
class ThemePreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
) : ThemePreferenceRepository {
    override val isDarkTheme: StateFlow<Boolean> = context.themeDataStore.data
        .map { preferences -> preferences[DARK_THEME_KEY] ?: false }
        .stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = false,
        )

    override suspend fun setDarkTheme(enabled: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }
}
