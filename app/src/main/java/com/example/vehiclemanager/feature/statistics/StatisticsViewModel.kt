package com.example.vehiclemanager.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.CalculateVehicleStatsUseCase
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository
import com.example.vehiclemanager.core.domain.StatsPeriod
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleStats
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    activeVehicleRepository: ActiveVehicleRepository,
    private val fuelRecordRepository: FuelRecordRepository,
    private val maintenanceRecordRepository: MaintenanceRecordRepository,
    private val calculateVehicleStats: CalculateVehicleStatsUseCase,
) : ViewModel() {
    private val selectedPeriod = MutableStateFlow(StatsPeriod.ALL_TIME)

    val uiState: StateFlow<StatisticsUiState> = activeVehicleRepository.activeVehicle
        .flatMapLatest { vehicle ->
            vehicle?.let { activeVehicle ->
                combine(
                    fuelRecordRepository.observeFuelRecords(activeVehicle.id),
                    maintenanceRecordRepository.observeMaintenanceRecords(activeVehicle.id),
                    selectedPeriod,
                ) { fuelRecords, maintenanceRecords, period ->
                    activeVehicle to calculateVehicleStats(
                        fuelRecords = fuelRecords,
                        maintenanceRecords = maintenanceRecords,
                        period = period,
                        currentDateMs = System.currentTimeMillis(),
                    )
                }
            } ?: selectedPeriod.map { period ->
                null to calculateVehicleStats(emptyList(), emptyList(), period, System.currentTimeMillis())
            }
        }
        .map { (vehicle, stats) ->
            StatisticsUiState(
                activeVehicle = vehicle,
                selectedPeriod = stats.period,
                stats = stats,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = StatisticsUiState(),
        )

    fun selectPeriod(period: StatsPeriod) {
        selectedPeriod.value = period
    }
}

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val activeVehicle: Vehicle? = null,
    val selectedPeriod: StatsPeriod = StatsPeriod.ALL_TIME,
    val stats: VehicleStats = VehicleStats(StatsPeriod.ALL_TIME),
)
