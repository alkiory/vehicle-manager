// MaintenanceScreen.kt
package com.example.vehiclemanager.feature.maintenance

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
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
fun MaintenanceScreen(
    onAddMaintenance: () -> Unit = {},
) {
    VehicleManagerScreen(
        topBar = { VehicleManagerAppBar(title = "Mantenimiento") },
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
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = "Aún no hay servicios",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = "Registra un servicio para mantener tu historial al día.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            Button(onClick = onAddMaintenance, modifier = Modifier.padding(top = 20.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(text = "  Añadir servicio")
            }
        }
    }
}