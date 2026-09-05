package com.example.vehiclemanager.feature.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.domain.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FuelHistoryViewModel @Inject constructor(
    activeVehicleRepository: ActiveVehicleRepository,
    private val fuelRecordRepository: FuelRecordRepository,
) : ViewModel() {
    private val pendingDelete = MutableStateFlow<FuelRecord?>(null)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FuelHistoryUiState> = activeVehicleRepository.activeVehicle
        .flatMapLatest { vehicle ->
            vehicle?.let { activeVehicle ->
                fuelRecordRepository.observeFuelRecords(activeVehicle.id)
                    .map { records -> activeVehicle to records }
            } ?: flowOf(null to emptyList())
        }
        .combine(pendingDelete) { (vehicle, records), recordToDelete ->
            FuelHistoryUiState(
                activeVehicle = vehicle,
                records = records,
                recordPendingDeletion = recordToDelete,
                isLoading = false,
            )
        }
        .combine(error) { state, deleteError -> state.copy(error = deleteError) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = FuelHistoryUiState(),
        )

    fun requestDelete(record: FuelRecord) {
        pendingDelete.value = record
    }

    fun dismissDelete() {
        pendingDelete.value = null
    }

    fun confirmDelete() {
        val record = pendingDelete.value ?: return
        pendingDelete.value = null
        viewModelScope.launch {
            runCatching { fuelRecordRepository.deleteFuelRecord(record) }
                .onFailure { throwable ->
                    // The next history refresh remains the source of truth. Keep
                    // the error visible without reintroducing the deleted item.
                    error.value = throwable.message ?: "delete_failed"
                }
        }
    }

    fun consumeError() {
        error.value = null
    }
}

data class FuelHistoryUiState(
    val isLoading: Boolean = true,
    val activeVehicle: Vehicle? = null,
    val records: List<FuelRecord> = emptyList(),
    val recordPendingDeletion: FuelRecord? = null,
    val error: String? = null,
)
