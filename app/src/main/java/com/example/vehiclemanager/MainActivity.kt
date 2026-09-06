package com.example.vehiclemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.R
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
        // Install splash screen before setting content
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themePreferenceRepository.isDarkTheme.collectAsState()
            VehicleManagerTheme(darkTheme = isDarkTheme) {
                VehicleManagerApp(splashScreen = splashScreen)
            }
        }
    }
}

@Composable
private fun VehicleManagerApp(splashScreen: androidx.core.splashscreen.SplashScreen) {
    // Keep splash screen visible until initial composition is ready
    LaunchedEffect(Unit) {
        // Signal that we're done with splash screen
        splashScreen.setKeepOnScreenCondition { false }
    }
    AppScaffold()
}

/**
 * Animated splash screen composable for app launch transition.
 * Shows the app icon with scale and fade animation before transitioning to main content.
 */
@Composable
fun SplashScreenAnimation(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onComplete: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "splash_scale",
    )
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "splash_alpha",
    )

    LaunchedEffect(scale, alpha) {
        if (scale >= 0.99f && alpha >= 0.99f) {
            onComplete()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Vehicle Manager Logo",
            modifier = Modifier.size(120.dp),
        )
    }
}
