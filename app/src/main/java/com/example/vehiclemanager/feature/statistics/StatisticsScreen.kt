package com.example.vehiclemanager.feature.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.core.domain.MonthlyExpenditure
import com.example.vehiclemanager.core.domain.MonthlyFuelPrice
import com.example.vehiclemanager.core.domain.StatsPeriod
import com.example.vehiclemanager.core.domain.VehicleStats
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    VehicleManagerScreen(
        topBar = {
            VehicleManagerAppBar(
                title = uiState.activeVehicle?.name?.let { "$it · Statistics" } ?: "Statistics",
            )
        },
    ) {
        if (uiState.activeVehicle == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Add a vehicle to view statistics.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            StatisticsContent(
                stats = uiState.stats,
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = viewModel::selectPeriod,
            )
        }
    }
}

@Composable
private fun StatisticsContent(
    stats: VehicleStats,
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = onPeriodSelected,
            )
        }
        item { MetricsCard(stats = stats) }
        item {
            ExpenditureChart(
                values = stats.monthlyExpenditure,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            FuelPriceChart(
                values = stats.monthlyFuelPrice,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit,
) {
    Column {
        Text(text = "Period", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatsPeriod.entries.forEach { period ->
                FilterChip(
                    selected = period == selectedPeriod,
                    onClick = { onPeriodSelected(period) },
                    label = { Text(text = period.displayName) },
                )
            }
        }
    }
}

@Composable
private fun MetricsCard(stats: VehicleStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Summary", style = MaterialTheme.typography.titleLarge)
            MetricRow("Total cost", formatCents(stats.totalCostCents))
            MetricRow("Fuel", formatCents(stats.totalFuelCostCents))
            MetricRow("Maintenance", formatCents(stats.totalMaintenanceCostCents))
            MetricRow("Distance", "${stats.totalDistanceKm} km")
            MetricRow(
                "Cost per kilometer",
                stats.costPerKilometerCentsX100?.let { formatHundredthsCents(it) } ?: "Not available",
            )
            MetricRow("Average monthly spend", formatCents(stats.averageMonthlySpendCents))
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ExpenditureChart(values: List<MonthlyExpenditure>, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Monthly expenditure", style = MaterialTheme.typography.titleLarge)
            if (values.isEmpty()) {
                Text(
                    text = "No expenditure data for this period.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else {
                BarChart(
                    values = values.map { it.totalCostCents },
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(top = 12.dp),
                )
                Text(
                    text = values.joinToString("  ·  ") { "${it.month}/${it.year}: ${formatCents(it.totalCostCents)}" },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun FuelPriceChart(values: List<MonthlyFuelPrice>, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Fuel price trend", style = MaterialTheme.typography.titleLarge)
            if (values.isEmpty()) {
                Text(
                    text = "No fuel price data for this period.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else {
                LineChart(
                    values = values.map { it.averagePricePerLiterCents },
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(top = 12.dp),
                )
                Text(
                    text = values.joinToString("  ·  ") {
                        "${it.month}/${it.year}: ${formatCents(it.averagePricePerLiterCents)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun BarChart(values: List<Long>, color: Color, modifier: Modifier = Modifier) {
    val maximum = remember(values) { values.maxOrNull()?.coerceAtLeast(1L) ?: 1L }
    Canvas(modifier = modifier) {
        val slotWidth = size.width / values.size.coerceAtLeast(1)
        val barWidth = slotWidth * 0.62f
        values.forEachIndexed { index, value ->
            val height = size.height * (value.toFloat() / maximum.toFloat())
            drawRect(
                color = color,
                topLeft = Offset(index * slotWidth + (slotWidth - barWidth) / 2f, size.height - height),
                size = androidx.compose.ui.geometry.Size(barWidth, height),
            )
        }
    }
}

@Composable
private fun LineChart(values: List<Long>, color: Color, modifier: Modifier = Modifier) {
    val maximum = remember(values) { values.maxOrNull()?.coerceAtLeast(1L) ?: 1L }
    val minimum = remember(values) { values.minOrNull() ?: 0L }
    Canvas(modifier = modifier) {
        if (values.size == 1) {
            val y = size.height / 2f
            drawCircle(color = color, radius = 6.dp.toPx(), center = Offset(size.width / 2f, y))
        } else {
            val range = (maximum - minimum).coerceAtLeast(1L).toFloat()
            val step = size.width / (values.size - 1).toFloat()
            val points = values.mapIndexed { index, value ->
                Offset(
                    x = index * step,
                    y = size.height - ((value - minimum).toFloat() / range * size.height),
                )
            }
            points.zipWithNext().forEach { (start, end) ->
                drawLine(color = color, start = start, end = end, strokeWidth = 4.dp.toPx())
            }
            points.forEach { point -> drawCircle(color = color, radius = 5.dp.toPx(), center = point) }
        }
    }
}

private val StatsPeriod.displayName: String
    get() = when (this) {
        StatsPeriod.LAST_30_DAYS -> "30 days"
        StatsPeriod.LAST_6_MONTHS -> "6 months"
        StatsPeriod.YEAR_TO_DATE -> "YTD"
        StatsPeriod.ALL_TIME -> "All time"
    }

private fun formatCents(cents: Long): String =
    String.format(Locale.US, "%.2f", cents / 100.0)

private fun formatHundredthsCents(value: Long): String =
    String.format(Locale.US, "%.2f", value / 100.0)
