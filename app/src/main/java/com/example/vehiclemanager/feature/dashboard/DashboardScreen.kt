package com.example.vehiclemanager.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.core.domain.ServiceStatus
import com.example.vehiclemanager.core.domain.UpcomingService
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import java.text.DateFormat
import java.util.Date

@Composable
fun DashboardScreen(
    onAddFuel: () -> Unit = {},
    onAddMaintenance: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var actionsExpanded by remember { mutableStateOf(false) }

    VehicleManagerScreen(
        topBar = { VehicleManagerAppBar(title = "Dashboard") },
        floatingActionButton = {
            DashboardActionHub(
                expanded = actionsExpanded,
                onExpandedChange = { actionsExpanded = it },
                onAddFuel = onAddFuel,
                onAddMaintenance = onAddMaintenance,
            )
        },
    ) {
        DashboardContent(uiState = uiState, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier,
) {
    val vehicle = uiState.activeVehicle
    if (vehicle == null) {
        Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No active vehicle", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Add a vehicle to see fuel economy and service health.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { VehicleSummaryCard(vehicleName = vehicle.name, odometerKm = vehicle.primaryOdometerKm) }
        item {
            FuelSummaryCard(
                latestFuel = uiState.latestFuelRecord,
                consumptionX100 = uiState.averageConsumptionLitersPer100KmX100,
            )
        }
        item { MaintenanceAlertCard(alerts = uiState.maintenanceAlerts) }
    }
}

@Composable
private fun VehicleSummaryCard(vehicleName: String, odometerKm: Long) {
    DashboardCard {
        Text(text = "Active vehicle", style = MaterialTheme.typography.labelLarge)
        Text(
            text = vehicleName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = "Odometer: $odometerKm km",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun FuelSummaryCard(
    latestFuel: com.example.vehiclemanager.core.domain.FuelRecord?,
    consumptionX100: Long?,
) {
    val formatter = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    DashboardCard {
        Text(text = "Fuel overview", style = MaterialTheme.typography.titleLarge)
        Text(
            text = consumptionX100?.let { "Average consumption: ${formatDecimalHundredths(it)} L/100 km" }
                ?: "Average consumption: insufficient data",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        if (latestFuel == null) {
            Text(
                text = "No refuels recorded yet.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        } else {
            Text(
                text = "Latest refuel: ${formatLiters(latestFuel.litersX100)} L · ${formatter.format(Date(latestFuel.timestampMs))}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun MaintenanceAlertCard(alerts: List<UpcomingService>) {
    DashboardCard {
        Text(text = "Maintenance", style = MaterialTheme.typography.titleLarge)
        if (alerts.isEmpty()) {
            Text(
                text = "No upcoming service alerts.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            alerts.take(MAX_ALERTS).forEach { alert ->
                MaintenanceAlertRow(alert = alert)
            }
            if (alerts.size > MAX_ALERTS) {
                Text(
                    text = "${alerts.size - MAX_ALERTS} more alert(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun MaintenanceAlertRow(alert: UpcomingService) {
    val color = if (alert.status == ServiceStatus.OVERDUE) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = alert.schedule.serviceTitle, fontWeight = FontWeight.Medium)
            Text(
                text = alert.status.displayName,
                color = color,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        alert.distanceDueKm?.let { Text(text = "$it km") }
    }
}

@Composable
private fun DashboardCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun DashboardActionHub(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAddFuel: () -> Unit,
    onAddMaintenance: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (expanded) {
            ExtendedFloatingActionButton(
                onClick = {
                    onExpandedChange(false)
                    onAddFuel()
                },
                text = { Text(text = "Add fuel") },
                icon = { Text(text = "+") },
                modifier = Modifier.semantics { contentDescription = "Add fuel" },
            )
            ExtendedFloatingActionButton(
                onClick = {
                    onExpandedChange(false)
                    onAddMaintenance()
                },
                text = { Text(text = "Add service") },
                icon = { Text(text = "+") },
                modifier = Modifier.semantics { contentDescription = "Add service" },
            )
        }
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.semantics { contentDescription = "Quick actions" },
        ) {
            Text(text = if (expanded) "×" else "+")
        }
    }
}

private val ServiceStatus.displayName: String
    get() = when (this) {
        ServiceStatus.DUE_SOON -> "Due soon"
        ServiceStatus.OVERDUE -> "Overdue"
        ServiceStatus.OK -> "OK"
    }

private fun formatDecimalHundredths(value: Long): String =
    "${value / 100}.${(value % 100).toString().padStart(2, '0')}"

private fun formatLiters(value: Int): String =
    "${value / 100}.${(value % 100).toString().padStart(2, '0')}"

private const val MAX_ALERTS = 3
