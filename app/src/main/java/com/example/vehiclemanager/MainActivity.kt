package com.example.vehiclemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
        super.onCreate(savedInstanceState) // Required to call super
        // Install splash screen before setting content
        val splashScreen = installSplashScreen()
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
    // The splash screen is handled by the Android system via installSplashScreen()
    // We just need to show the main content immediately
    AppScaffold()
}
