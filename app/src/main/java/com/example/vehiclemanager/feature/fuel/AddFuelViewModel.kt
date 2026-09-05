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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddFuelViewModel @Inject constructor(
    private val activeVehicleRepository: ActiveVehicleRepository,
    private val fuelRecordRepository: FuelRecordRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddFuelUiState())
    val uiState: StateFlow<AddFuelUiState> = _uiState.asStateFlow()

    private val activeVehicleRecords = activeVehicleRepository.activeVehicle
        .flatMapLatest { vehicle ->
            vehicle?.let { fuelRecordRepository.observeFuelRecords(it.id) }
                ?: flowOf(emptyList())
        }

    init {
        viewModelScope.launch {
            combine(
                activeVehicleRepository.activeVehicle,
                activeVehicleRecords,
            ) { vehicle, records -> vehicle to records }
                .collect { (vehicle, records) ->
                    val previousOdometer = vehicle?.let { activeVehicle ->
                        maxOf(
                            activeVehicle.primaryOdometerKm,
                            records.maxOfOrNull(FuelRecord::odometerKm) ?: 0L,
                        )
                    }
                    _uiState.update { state ->
                        val shouldPrefillOdometer = !state.odometerManuallyEdited
                        state.copy(
                            isLoading = false,
                            activeVehicle = vehicle,
                            previousOdometerKm = previousOdometer,
                            form = if (shouldPrefillOdometer && previousOdometer != null) {
                                state.form.copy(odometerKm = previousOdometer.toString())
                            } else {
                                state.form
                            },
                            noActiveVehicle = vehicle == null,
                        )
                    }
                }
        }
    }

    fun updateTimestamp(value: Long) = updateForm { copy(timestampMs = value) }

    fun updateOdometer(value: String) {
        _uiState.update { state ->
            state.copy(
                form = state.form.copy(odometerKm = value),
                odometerManuallyEdited = true,
                errors = FuelValidationErrors(),
                saveError = null,
            )
        }
    }

    fun updateLiters(value: String) = updateForm { copy(liters = value) }

    fun updatePricePerLiter(value: String) = updateForm { copy(pricePerLiter = value) }

    fun updateFullTank(value: Boolean) = updateForm { copy(isFullTank = value) }

    fun updateStationName(value: String) = updateForm { copy(stationName = value) }

    fun updateNotes(value: String) = updateForm { copy(notes = value) }

    fun save() {
        val state = _uiState.value
        val errors = state.form.validate(state.previousOdometerKm)
        if (state.activeVehicle == null) {
            _uiState.update { it.copy(noActiveVehicle = true, saveError = "no_active_vehicle") }
            return
        }
        if (errors.hasErrors) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        val litersX100 = litersToX100(state.form.liters)
        val pricePerLiterCents = calculatePricePerLiterCents(state.form.pricePerLiter)
        val totalCostCents = calculateTotalCostCents(
            liters = state.form.liters,
            pricePerLiter = state.form.pricePerLiter,
        )
        if (litersX100 == null || pricePerLiterCents == null || totalCostCents == null) {
            _uiState.update {
                it.copy(
                    errors = FuelValidationErrors(
                        liters = "invalid",
                        pricePerLiter = "invalid",
                    ),
                )
            }
            return
        }

        val record = FuelRecord(
            vehicleId = state.activeVehicle.id,
            timestampMs = state.form.timestampMs,
            odometerKm = state.form.odometerKm.toLong(),
            litersX100 = litersX100,
            pricePerLiterCents = pricePerLiterCents,
            totalCostCents = totalCostCents,
            isFullTank = state.form.isFullTank,
            stationName = state.form.stationName.trim().ifEmpty { null },
            notes = state.form.notes.trim().ifEmpty { null },
        )

        _uiState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            runCatching { fuelRecordRepository.insertFuelRecord(record) }
                .onSuccess { _uiState.update { it.copy(isSaving = false, saveCompleted = true) } }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveError = throwable.message ?: "save_failed",
                        )
                    }
                }
        }
    }

    fun consumeSaveCompleted() {
        _uiState.update { it.copy(saveCompleted = false) }
    }

    private fun updateForm(transform: FuelFormData.() -> FuelFormData) {
        _uiState.update { state ->
            state.copy(
                form = state.form.transform(),
                errors = FuelValidationErrors(),
                saveError = null,
            )
        }
    }
}

data class AddFuelUiState(
    val isLoading: Boolean = true,
    val activeVehicle: Vehicle? = null,
    val previousOdometerKm: Long? = null,
    val form: FuelFormData = FuelFormData(),
    val errors: FuelValidationErrors = FuelValidationErrors(),
    val odometerManuallyEdited: Boolean = false,
    val noActiveVehicle: Boolean = false,
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false,
    val saveError: String? = null,
)
