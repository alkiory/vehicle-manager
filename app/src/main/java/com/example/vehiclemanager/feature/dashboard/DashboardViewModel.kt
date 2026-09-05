package com.example.vehiclemanager.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.CalculateFuelConsumptionUseCase
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.domain.GetDashboardSummaryUseCase
import com.example.vehiclemanager.core.domain.MaintenanceScheduleRepository
import com.example.vehiclemanager.core.domain.DashboardSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    activeVehicleRepository: ActiveVehicleRepository,
    private val fuelRecordRepository: FuelRecordRepository,
    private val maintenanceScheduleRepository: MaintenanceScheduleRepository,
    private val getDashboardSummary: GetDashboardSummaryUseCase,
) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> = activeVehicleRepository.activeVehicle
        .flatMapLatest { vehicle ->
            vehicle?.let { activeVehicle ->
                combine(
                    fuelRecordRepository.observeFuelRecords(activeVehicle.id),
                    maintenanceScheduleRepository.observeSchedules(activeVehicle.id),
                ) { fuelRecords, schedules ->
                    getDashboardSummary(
                        activeVehicle = activeVehicle,
                        fuelRecords = fuelRecords,
                        schedules = schedules,
                        currentDateMs = System.currentTimeMillis(),
                    )
                }
            } ?: flowOf(getDashboardSummary(null, emptyList(), emptyList(), System.currentTimeMillis()))
        }
        .map(::DashboardUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = DashboardUiState(),
        )
}

data class DashboardUiState(
    val summary: DashboardSummary = DashboardSummary(),
    val isLoading: Boolean = false,
) {
    val activeVehicle get() = summary.activeVehicle
    val latestFuelRecord get() = summary.latestFuelRecord
    val averageConsumptionLitersPer100KmX100 get() = summary.averageConsumptionLitersPer100KmX100
    val maintenanceAlerts get() = summary.maintenanceAlerts
}
