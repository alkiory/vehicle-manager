package com.example.vehiclemanager.feature.maintenance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.MaintenanceCategory
import com.example.vehiclemanager.core.domain.MaintenanceRecord
import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository
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
class AddMaintenanceViewModel @Inject constructor(
    private val activeVehicleRepository: ActiveVehicleRepository,
    private val maintenanceRecordRepository: MaintenanceRecordRepository,
    private val vehicleRepository: VehicleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val recordId = savedStateHandle.get<Long>(RECORD_ID_KEY) ?: 0L
    private val _uiState = MutableStateFlow(
        AddMaintenanceUiState(isEditing = recordId != 0L, recordId = recordId),
    )
    val uiState: StateFlow<AddMaintenanceUiState> = _uiState.asStateFlow()

    init {
        if (recordId != 0L) loadRecord(recordId) else startCreateMode()
    }

    fun updateTitle(value: String) = updateForm { copy(title = value) }

    fun updateCategory(value: MaintenanceCategory) = updateForm { copy(category = value) }

    fun updateCost(value: String) = updateForm { copy(cost = value) }

    fun updateOdometer(value: String) = updateForm { copy(odometerKm = value) }

    fun updateTimestamp(value: Long) = updateForm { copy(timestampMs = value) }

    fun updateNotes(value: String) = updateForm { copy(notes = value) }

    fun updatePerformedBy(value: String) = updateForm { copy(performedBy = value) }

    fun save() {
        val state = _uiState.value
        val errors = state.form.validate(state.previousOdometerKm)
        val costCents = maintenanceCostToCents(state.form.cost)
        val odometerKm = state.form.odometerKm.toLongOrNull()
        val completeErrors = errors.copy(
            cost = errors.cost ?: if (costCents == null) "invalid" else null,
            odometer = errors.odometer ?: if (odometerKm == null) "invalid" else null,
        )

        if (completeErrors.hasErrors) {
            _uiState.update { it.copy(errors = completeErrors) }
            return
        }

        val activeVehicle = state.activeVehicle
        if (activeVehicle == null) {
            _uiState.update { it.copy(noActiveVehicle = true, saveError = "no_active_vehicle") }
            return
        }

        val serviceOdometerKm = odometerKm ?: return
        val record = MaintenanceRecord(
            id = state.recordId,
            vehicleId = activeVehicle.id,
            title = state.form.title.trim(),
            category = state.form.category,
            costCents = costCents ?: return,
            odometerKm = serviceOdometerKm,
            timestampMs = state.form.timestampMs,
            notes = state.form.notes.trim().ifEmpty { null },
            performedBy = state.form.performedBy.trim().ifEmpty { null },
        )

        _uiState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            runCatching {
                if (record.id == 0L) {
                    maintenanceRecordRepository.insertMaintenanceRecord(record)
                } else {
                    maintenanceRecordRepository.updateMaintenanceRecord(record)
                }
                if (serviceOdometerKm > activeVehicle.primaryOdometerKm) {
                    vehicleRepository.updateVehicle(
                        activeVehicle.copy(primaryOdometerKm = serviceOdometerKm),
                    )
                }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saveCompleted = true) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(isSaving = false, saveError = throwable.message ?: "save_failed")
                }
            }
        }
    }

    fun consumeSaveCompleted() = _uiState.update { it.copy(saveCompleted = false) }

    private fun startCreateMode() {
        viewModelScope.launch {
            activeVehicleRepository.activeVehicle.collect { vehicle ->
                _uiState.update { state ->
                    val shouldPrefill = !state.odometerManuallyEdited
                    state.copy(
                        isLoading = false,
                        activeVehicle = vehicle,
                        previousOdometerKm = vehicle?.primaryOdometerKm,
                        form = if (shouldPrefill && vehicle != null) {
                            state.form.copy(odometerKm = vehicle.primaryOdometerKm.toString())
                        } else state.form,
                        noActiveVehicle = vehicle == null,
                    )
                }
            }
        }
    }

    private fun loadRecord(id: Long) {
        viewModelScope.launch {
            val record = maintenanceRecordRepository.getMaintenanceRecord(id)
            val vehicle = activeVehicleRepository.activeVehicle.value
            if (record == null) {
                _uiState.update { it.copy(isLoading = false, loadError = "record_not_found") }
                return@launch
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    activeVehicle = vehicle,
                    recordId = record.id,
                    previousOdometerKm = vehicle?.primaryOdometerKm,
                    form = record.toFormData(),
                    noActiveVehicle = vehicle == null,
                )
            }
        }
    }

    private fun updateForm(transform: MaintenanceFormData.() -> MaintenanceFormData) {
        _uiState.update { state ->
            state.copy(
                form = state.form.transform(),
                errors = MaintenanceValidationErrors(),
                saveError = null,
                odometerManuallyEdited = true,
            )
        }
    }

    private companion object {
        const val RECORD_ID_KEY = "recordId"
    }
}

data class AddMaintenanceUiState(
    val isEditing: Boolean = false,
    val isLoading: Boolean = true,
    val activeVehicle: Vehicle? = null,
    val previousOdometerKm: Long? = null,
    val recordId: Long = 0,
    val form: MaintenanceFormData = MaintenanceFormData(),
    val errors: MaintenanceValidationErrors = MaintenanceValidationErrors(),
    val odometerManuallyEdited: Boolean = false,
    val noActiveVehicle: Boolean = false,
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false,
    val loadError: String? = null,
    val saveError: String? = null,
)

private fun MaintenanceRecord.toFormData() = MaintenanceFormData(
    title = title,
    category = category,
    cost = "${costCents / 100}.${(costCents % 100).toString().padStart(2, '0')}",
    odometerKm = odometerKm.toString(),
    timestampMs = timestampMs,
    notes = notes.orEmpty(),
    performedBy = performedBy.orEmpty(),
)
