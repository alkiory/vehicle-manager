// FuelScreen.kt
package com.example.vehiclemanager.feature.fuel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
        topBar = { VehicleManagerAppBar(title = "Combustible") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFuel) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir repostaje")
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
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Opacity,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = "Aún no hay repostajes",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = "Añade tu primer repostaje para empezar a ver tu consumo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            Button(onClick = onAddFuel, modifier = Modifier.padding(top = 20.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(text = "  Añadir repostaje")
            }
        }
    }
}