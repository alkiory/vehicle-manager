package com.example.vehiclemanager.feature.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import com.example.vehiclemanager.core.ui.navigation.FuelDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun FuelDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: FuelDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    VehicleManagerScreen(
        topBar = {
            VehicleManagerAppBar(
                title = "Refuel details",
                actions = {
                    TextButton(onClick = onNavigateBack) {
                        Text(text = "Back")
                    }
                },
            )
        },
    ) {
        val record = uiState.record
        when {
            uiState.isLoading -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            }
            uiState.record == null -> Text(
                text = "Fuel record not found.",
                modifier = Modifier.padding(24.dp),
            )
            else -> FuelDetailContent(record!!)
        }
    }
}

@Composable
private fun FuelDetailContent(record: FuelRecord) {
    val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = dateFormatter.format(Date(record.timestampMs)),
            style = MaterialTheme.typography.headlineSmall,
        )
        DetailRow("Fuel quantity", formatLiters(record.litersX100))
        DetailRow("Price per liter", formatCents(record.pricePerLiterCents))
        DetailRow("Total cost", formatCents(record.totalCostCents))
        DetailRow("Odometer", "${record.odometerKm} km")
        DetailRow("Tank status", if (record.isFullTank) "Full tank" else "Partial refuel")
        record.stationName?.let { DetailRow("Station", it) }
        record.notes?.let { DetailRow("Notes", it) }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    ListItem(
        headlineContent = { Text(text = label) },
        supportingContent = { Text(text = value) },
    )
}

@HiltViewModel
class FuelDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fuelRecordRepository: FuelRecordRepository,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<FuelDetailRoute>()
    private val _uiState = MutableStateFlow(FuelDetailUiState())
    val uiState: StateFlow<FuelDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = FuelDetailUiState(
                isLoading = false,
                record = fuelRecordRepository.getFuelRecord(route.fuelRecordId),
            )
        }
    }
}

data class FuelDetailUiState(
    val isLoading: Boolean = true,
    val record: FuelRecord? = null,
)

private fun formatLiters(litersX100: Int): String {
    val whole = litersX100 / 100
    val fraction = (litersX100 % 100).toString().padStart(2, '0')
    return "$whole.$fraction L"
}

private fun formatCents(cents: Long): String =
    "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
