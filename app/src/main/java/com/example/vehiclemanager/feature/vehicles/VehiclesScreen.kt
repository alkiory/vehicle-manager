package com.example.vehiclemanager.feature.vehicles

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.R
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import kotlinx.coroutines.launch

@Composable
fun VehiclesScreen(
    onAddVehicle: () -> Unit = {},
    onOpenVehicle: (Long) -> Unit = {},
    viewModel: VehicleBackupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val vehiclesState by viewModel.vehicles.collectAsState()
    val activeVehicleId by viewModel.activeVehicleId.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        val json = uiState.exportJson
        if (uri != null && json != null) {
            scope.launch {
                writeBackup(context, uri, json)
                viewModel.consumeExportJson()
            }
        }
    }
    val openBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val json = readBackup(context, uri)
                if (json == null) {
                    viewModel.importBackup("invalid")
                } else {
                    viewModel.importBackup(json)
                }
            }
        }
    }

    LaunchedEffect(uiState.exportJson) {
        if (uiState.exportJson != null) {
            createBackupLauncher.launch(context.getString(R.string.backup_export_filename))
        }
    }

    VehicleManagerScreen(
        topBar = { VehicleManagerAppBar(title = "Vehículos") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddVehicle,
                modifier = Modifier.semantics {
                    contentDescription = "Añadir vehículo"
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text(
                        text = "GESTIÓN",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Tus vehículos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Gestiona la información, el historial y los datos de respaldo de tu flota.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            val vehicles = vehiclesState
            if (vehicles == null) {
                item { VehicleListLoadingHint() }
            } else if (vehicles.isEmpty()) {
                item { VehicleListEmptyHint(onAddVehicle = onAddVehicle) }
            } else {
                items(vehicles, key = Vehicle::id) { vehicle ->
                    VehicleRow(
                        vehicle = vehicle,
                        isActive = vehicle.id == activeVehicleId,
                        onClick = { onOpenVehicle(vehicle.id) },
                        onActivate = { viewModel.setActiveVehicle(vehicle.id) },
                    )
                }
            }
            item {
                BackupCard(
                    isBusy = uiState.isBusy,
                    onExport = viewModel::exportBackup,
                    onImport = { openBackupLauncher.launch(arrayOf("application/json", "text/json")) },
                )
            }
            item {
                when {
                    uiState.importCompleted -> Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Backup importado correctamente.",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                    uiState.error != null -> Text(
                        text = stringResource(R.string.backup_error, uiState.error.orEmpty()),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleListLoadingHint() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun VehicleListEmptyHint(onAddVehicle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = "Aún no hay vehículos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = "Añade tu primer vehículo para empezar a registrar repostajes y servicios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            OutlinedButton(
                onClick = onAddVehicle,
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(text = "  Añadir vehículo")
            }
        }
    }
}

@Composable
private fun VehicleRow(
    vehicle: Vehicle,
    isActive: Boolean,
    onClick: () -> Unit,
    onActivate: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Editar ${vehicle.name}" },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    text = vehicle.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${vehicle.make} ${vehicle.model} · ${vehicle.primaryOdometerKm} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            if (isActive) {
                StatusPill()
            } else {
                OutlinedButton(onClick = onActivate) {
                    Text(text = "Activar")
                }
            }
        }
    }
}

@Composable
private fun StatusPill() {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(MaterialTheme.colorScheme.onPrimary, CircleShape),
        )
        Text(
            text = "  Activo",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun BackupCard(
    isBusy: Boolean,
    onExport: () -> Unit,
    onImport: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Backup,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = stringResource(R.string.backup_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
            Text(
                text = stringResource(R.string.backup_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )

            if (isBusy) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally))
                Text(
                    text = stringResource(R.string.backup_working),
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                Button(
                    onClick = onExport,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).semantics {
                        contentDescription = "Exportar backup de datos"
                    },
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(text = "  " + stringResource(R.string.export_backup))
                }
                OutlinedButton(
                    onClick = onImport,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).semantics {
                        contentDescription = "Importar backup de datos"
                    },
                ) {
                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(text = "  " + stringResource(R.string.import_backup))
                }
            }
        }
    }
}

private fun writeBackup(context: Context, uri: Uri, json: String) {
    context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
        writer.write(json)
    }
}

private fun readBackup(context: Context, uri: Uri): String? = runCatching {
    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
}.getOrNull()
