package com.example.vehiclemanager.feature.vehicles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.vehiclemanager.core.ui.navigation.AddEditVehicleRoute
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<AddEditVehicleRoute>()
    private val currentYear = LocalDate.now().year
    private val _uiState = MutableStateFlow(
        AddEditVehicleUiState(isEditing = route.vehicleId != null),
    )
    val uiState: StateFlow<AddEditVehicleUiState> = _uiState.asStateFlow()

    init {
        route.vehicleId?.let(::loadVehicle)
            ?: _uiState.update { it.copy(isLoading = false) }
    }

    fun updateName(value: String) = updateForm { copy(name = value) }

    fun updateMake(value: String) = updateForm { copy(make = value) }

    fun updateModel(value: String) = updateForm { copy(model = value) }

    fun updateYear(value: String) = updateForm { copy(year = value) }

    fun updateLicensePlate(value: String) = updateForm { copy(licensePlate = value) }

    fun updateVin(value: String) = updateForm { copy(vin = value) }

    fun updateFuelType(value: FuelType) = updateForm { copy(fuelType = value) }

    fun updateOdometer(value: String) = updateForm { copy(primaryOdometerKm = value) }

    fun save() {
        val form = _uiState.value.form
        val errors = form.validate(currentYear)
        if (errors.hasErrors) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        val vehicle = form.toVehicle(_uiState.value.vehicleId)
        _uiState.update { it.copy(errors = VehicleValidationErrors(), isSaving = true, saveError = null) }
        viewModelScope.launch {
            runCatching {
                if (vehicle.id == 0L) {
                    vehicleRepository.insertVehicle(vehicle)
                } else {
                    vehicleRepository.updateVehicle(vehicle)
                }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saveCompleted = true) }
            }.onFailure { throwable ->
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

    private fun loadVehicle(vehicleId: Long) {
        viewModelScope.launch {
            vehicleRepository.getVehicle(vehicleId)
                ?.let { vehicle ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            vehicleId = vehicle.id,
                            form = vehicle.toFormData(),
                        )
                    }
                }
                ?: _uiState.update { it.copy(isLoading = false, loadError = "vehicle_not_found") }
        }
    }

    private fun updateForm(transform: VehicleFormData.() -> VehicleFormData) {
        _uiState.update { state ->
            state.copy(
                form = state.form.transform(),
                errors = VehicleValidationErrors(),
                saveError = null,
            )
        }
    }
}

data class AddEditVehicleUiState(
    val isEditing: Boolean = false,
    val isLoading: Boolean = true,
    val vehicleId: Long = 0,
    val form: VehicleFormData = VehicleFormData(),
    val errors: VehicleValidationErrors = VehicleValidationErrors(),
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false,
    val loadError: String? = null,
    val saveError: String? = null,
)

private fun VehicleFormData.toVehicle(id: Long): Vehicle = Vehicle(
    id = id,
    name = name.trim(),
    make = make.trim(),
    model = model.trim(),
    year = year.toInt(),
    licensePlate = licensePlate.trim(),
    vin = vin.trim().ifEmpty { null },
    fuelType = fuelType,
    primaryOdometerKm = primaryOdometerKm.toLong(),
)

private fun Vehicle.toFormData(): VehicleFormData = VehicleFormData(
    name = name,
    make = make,
    model = model,
    year = year.toString(),
    licensePlate = licensePlate,
    vin = vin.orEmpty(),
    fuelType = fuelType,
    primaryOdometerKm = primaryOdometerKm.toString(),
)
