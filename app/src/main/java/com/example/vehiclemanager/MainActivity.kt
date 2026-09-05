package com.example.vehiclemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.vehiclemanager.core.ui.navigation.AppScaffold
import com.example.vehiclemanager.core.ui.theme.VehicleManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleManagerTheme {
                VehicleManagerApp()
            }
        }
    }
}

@Composable
private fun VehicleManagerApp() {
    AppScaffold()
}

@Preview(showBackground = true)
@Composable
private fun VehicleManagerPreview() {
    VehicleManagerTheme {
        VehicleManagerApp()
    }
}
