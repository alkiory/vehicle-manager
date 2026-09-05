package com.example.vehiclemanager.feature.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelHistoryScreen(
    onNavigateBack: (() -> Unit)? = null,
    onAddFuel: () -> Unit = {},
    onOpenDetail: (Long) -> Unit,
    viewModel: FuelHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) viewModel.consumeError()
    }

    VehicleManagerScreen(
        topBar = {
            VehicleManagerAppBar(
                title = uiState.activeVehicle?.name?.let { "$it · Fuel" } ?: "Fuel history",
                actions = {
                    onNavigateBack?.let { onBack ->
                        TextButton(onClick = onBack) {
                            Text(text = "Back")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFuel) {
                Text(text = "+")
            }
        },
    ) {
        when {
            uiState.isLoading -> LoadingContent()
            uiState.activeVehicle == null -> EmptyFuelContent("Add a vehicle to view fuel history.")
            uiState.records.isEmpty() -> EmptyFuelContent("No refuels recorded yet.")
            else -> FuelRecordList(
                records = uiState.records,
                onOpenDetail = onOpenDetail,
                onRequestDelete = viewModel::requestDelete,
            )
        }
    }

    uiState.recordPendingDeletion?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            title = { Text(text = "Delete refuel?") },
            text = { Text(text = "This fuel record will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDelete) {
                    Text(text = "Cancel")
                }
            },
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyFuelContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelRecordList(
    records: List<FuelRecord>,
    onOpenDetail: (Long) -> Unit,
    onRequestDelete: (FuelRecord) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(records, key = FuelRecord::id) { record ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                        onRequestDelete(record)
                    }
                    false
                },
            )
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                backgroundContent = { DeleteBackground() },
                content = {
                    FuelRecordCard(record = record, onClick = { onOpenDetail(record.id) })
                },
            )
        }
    }
}

@Composable
private fun DeleteBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Delete fuel record" }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = "Delete",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun FuelRecordCard(
    record: FuelRecord,
    onClick: () -> Unit,
) {
    val dateFormatter = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ListItem(
            headlineContent = {
                Text(text = dateFormatter.format(Date(record.timestampMs)))
            },
            supportingContent = {
                Text(text = "${formatLiters(record.litersX100)} · ${record.odometerKm} km")
            },
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = formatCents(record.totalCostCents))
                    Text(
                        text = if (record.isFullTank) "Full tank" else "Partial",
                        color = if (record.isFullTank) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            },
        )
    }
}

private fun formatLiters(litersX100: Int): String {
    val whole = litersX100 / 100
    val fraction = (litersX100 % 100).toString().padStart(2, '0')
    return "$whole.$fraction L"
}

private fun formatCents(cents: Long): String =
    "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
