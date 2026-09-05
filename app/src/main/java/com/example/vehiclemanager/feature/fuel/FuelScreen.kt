package com.example.vehiclemanager.feature.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen

@Composable
fun FuelScreen(
    onAddFuel: () -> Unit = {},
) {
    VehicleManagerScreen(
        topBar = { VehicleManagerAppBar(title = "Fuel") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFuel) {
                Text(text = "+")
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Fuel",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Track refuels and consumption here.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
