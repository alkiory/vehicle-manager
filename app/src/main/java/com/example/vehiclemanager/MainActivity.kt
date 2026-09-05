package com.example.vehiclemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.core.domain.ThemePreferenceRepository
import com.example.vehiclemanager.core.ui.navigation.AppScaffold
import com.example.vehiclemanager.core.ui.theme.VehicleManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themePreferenceRepository: ThemePreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themePreferenceRepository.isDarkTheme.collectAsState()
            VehicleManagerTheme(darkTheme = isDarkTheme) {
                VehicleManagerApp()
            }
        }
    }
}

@Composable
private fun VehicleManagerApp() {
    AppScaffold()
}
