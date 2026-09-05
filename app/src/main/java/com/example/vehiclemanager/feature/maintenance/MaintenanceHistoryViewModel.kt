package com.example.vehiclemanager.feature.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.MaintenanceRecord
import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MaintenanceHistoryViewModel @Inject constructor(
    activeVehicleRepository: ActiveVehicleRepository,
    private val maintenanceRecordRepository: MaintenanceRecordRepository,
) : ViewModel() {
    private val pendingDelete = MutableStateFlow<MaintenanceRecord?>(null)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MaintenanceHistoryUiState> = activeVehicleRepository.activeVehicle
        .flatMapLatest { vehicle ->
            vehicle?.let { activeVehicle ->
                maintenanceRecordRepository.observeMaintenanceRecords(activeVehicle.id)
                    .map { records -> activeVehicle to records }
            } ?: flowOf(null to emptyList())
        }
        .combine(pendingDelete) { (vehicle, records), record ->
            MaintenanceHistoryUiState(
                isLoading = false,
                activeVehicle = vehicle,
                records = records,
                recordPendingDeletion = record,
            )
        }
        .combine(error) { state, deleteError -> state.copy(error = deleteError) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = MaintenanceHistoryUiState(),
        )

    fun requestDelete(record: MaintenanceRecord) {
        pendingDelete.value = record
    }

    fun dismissDelete() {
        pendingDelete.value = null
    }

    fun confirmDelete() {
        val record = pendingDelete.value ?: return
        pendingDelete.value = null
        viewModelScope.launch {
            runCatching { maintenanceRecordRepository.deleteMaintenanceRecord(record) }
                .onFailure { throwable -> error.value = throwable.message ?: "delete_failed" }
        }
    }

    fun consumeError() {
        error.value = null
    }
}

data class MaintenanceHistoryUiState(
    val isLoading: Boolean = true,
    val activeVehicle: Vehicle? = null,
    val records: List<MaintenanceRecord> = emptyList(),
    val recordPendingDeletion: MaintenanceRecord? = null,
    val error: String? = null,
)
