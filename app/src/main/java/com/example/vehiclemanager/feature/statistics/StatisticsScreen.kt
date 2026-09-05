package com.example.vehiclemanager.feature.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen

@Composable
fun StatisticsScreen() {
    VehicleManagerScreen(
        topBar = { VehicleManagerAppBar(title = "Statistics") },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Vehicle trends and analytics will appear here.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
