package com.example.vehiclemanager.feature.maintenance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.core.domain.MaintenanceCategory
import com.example.vehiclemanager.core.domain.MaintenanceRecord
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import java.text.DateFormat
import java.util.Date

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceHistoryScreen(
    onAddMaintenance: () -> Unit,
    onOpenEdit: (Long) -> Unit,
    viewModel: MaintenanceHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) viewModel.consumeError()
    }

    VehicleManagerScreen(
        topBar = { VehicleManagerAppBar(title = uiState.activeVehicle?.name?.let { "$it · Servicios" } ?: "Mantenimiento") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMaintenance) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir servicio")
            }
        },
    ) {
        when {
            uiState.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            uiState.activeVehicle == null -> EmptyMaintenanceContent("Añade un vehículo para ver el historial de mantenimiento.")
            uiState.records.isEmpty() -> EmptyMaintenanceContent("Aún no hay registros de servicio.")
            else -> MaintenanceRecordList(
                records = uiState.records,
                onOpenEdit = onOpenEdit,
                onRequestDelete = viewModel::requestDelete,
            )
        }
    }

    uiState.recordPendingDeletion?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            title = { Text(text = "¿Eliminar registro de servicio?") },
            text = { Text(text = "Este registro de mantenimiento se eliminará permanentemente.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text(text = "Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = viewModel::dismissDelete) { Text(text = "Cancelar") } },
        )
    }
}

@Composable
private fun EmptyMaintenanceContent(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun MaintenanceRecordList(
    records: List<MaintenanceRecord>,
    onOpenEdit: (Long) -> Unit,
    onRequestDelete: (MaintenanceRecord) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(records, key = MaintenanceRecord::id) { record ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) onRequestDelete(record)
                    false
                },
            )
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    Box(
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Eliminar registro de mantenimiento" },
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(text = "Eliminar", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                },
                content = { MaintenanceRecordCard(record, onClick = { onOpenEdit(record.id) }) },
            )
        }
    }
}

@Composable
private fun MaintenanceRecordCard(record: MaintenanceRecord, onClick: () -> Unit) {
    val formatter = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(text = record.title) },
            supportingContent = {
                Text(text = "${record.category.displayName} · ${record.odometerKm} km · ${formatter.format(Date(record.timestampMs))}")
            },
            trailingContent = { Text(text = formatCents(record.costCents)) },
        )
    }
}

private val MaintenanceCategory.displayName: String
    get() = name.lowercase().split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

private fun formatCents(cents: Long): String = "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
