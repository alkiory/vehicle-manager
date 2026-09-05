package com.example.vehiclemanager.feature.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.ExportDatabaseUseCase
import com.example.vehiclemanager.core.domain.ImportDatabaseUseCase
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VehicleBackupViewModel @Inject constructor(
    private val exportDatabase: ExportDatabaseUseCase,
    private val importDatabase: ImportDatabaseUseCase,
    vehicleRepository: VehicleRepository,
    private val activeVehicleRepository: ActiveVehicleRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleBackupUiState())
    val uiState: StateFlow<VehicleBackupUiState> = _uiState.asStateFlow()

    // `null` hasta la primera emisión de Room: evita mostrar el estado vacío antes de cargar.
    val vehicles: StateFlow<List<Vehicle>?> = vehicleRepository.vehicles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    val activeVehicleId: StateFlow<Long?> = activeVehicleRepository.activeVehicle
        .map { vehicle -> vehicle?.id }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    fun setActiveVehicle(vehicleId: Long) {
        viewModelScope.launch {
            activeVehicleRepository.setActiveVehicle(vehicleId)
        }
    }

    fun exportBackup() {
        _uiState.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            runCatching { exportDatabase() }
                .onSuccess { json -> _uiState.update { it.copy(isBusy = false, exportJson = json) } }
                .onFailure { error -> _uiState.update { it.copy(isBusy = false, error = error.message ?: "Backup export failed") } }
        }
    }

    fun consumeExportJson() {
        _uiState.update { it.copy(exportJson = null) }
    }

    fun importBackup(json: String) {
        _uiState.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            runCatching { importDatabase(json) }
                .onSuccess { _uiState.update { it.copy(isBusy = false, importCompleted = true) } }
                .onFailure { error -> _uiState.update { it.copy(isBusy = false, error = error.message ?: "Backup import failed") } }
        }
    }

    fun consumeImportCompleted() {
        _uiState.update { it.copy(importCompleted = false) }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class VehicleBackupUiState(
    val isBusy: Boolean = false,
    val exportJson: String? = null,
    val importCompleted: Boolean = false,
    val error: String? = null,
)
