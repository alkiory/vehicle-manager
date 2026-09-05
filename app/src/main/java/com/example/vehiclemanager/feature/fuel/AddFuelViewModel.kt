package com.example.vehiclemanager.feature.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.ui.navigation.FormDirtyStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val formDirtyStateHolder: FormDirtyStateHolder,
) : ViewModel() {
    private val dirtyToken = Any()
    private val _uiState = MutableStateFlow(AddFuelUiState())
    val uiState: StateFlow<AddFuelUiState> = _uiState.asStateFlow()

    private val activeVehicleRecords = activeVehicleRepository.activeVehicle
        .flatMapLatest { vehicle ->
            vehicle?.let { fuelRecordRepository.observeFuelRecords(it.id) }
                ?: flowOf(emptyList())
        }

    init {
        viewModelScope.launch {
            _uiState.collect { state ->
                formDirtyStateHolder.setDirty(dirtyToken, state.isDirty)
            }
        }
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
                isDirty = true,
                errors = FuelValidationErrors(),
                saveError = null,
            )
        }
    }

    fun updateLiters(value: String) = updateFuelAmounts(lastEditedField = FuelAmountField.LITERS, value = value)

    fun updatePricePerLiter(value: String) = updateFuelAmounts(lastEditedField = FuelAmountField.PRICE_PER_LITER, value = value)

    fun updateTotalCost(value: String) = updateFuelAmounts(lastEditedField = FuelAmountField.TOTAL_COST, value = value)

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
        val totalCostCents = calculateTotalCostCents(state.form.totalCost)
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
        _uiState.update { it.copy(saveCompleted = false, isDirty = false) }
    }

    override fun onCleared() {
        formDirtyStateHolder.setDirty(dirtyToken, false)
        super.onCleared()
    }

    private fun updateForm(transform: FuelFormData.() -> FuelFormData) {
        _uiState.update { state ->
            state.copy(
                form = state.form.transform(),
                isDirty = true,
                errors = FuelValidationErrors(),
                saveError = null,
            )
        }
    }

    /**
     * Dynamic refuel calculator: whenever any two of the three amount fields
     * (liters, price per liter, total cost) hold valid positive values entered
     * by the user, the missing third field is computed automatically.
     *
     * Only user edits feed the calculation, so an auto-filled value never
     * re-triggers it and no circular update loop is possible.
     */
    private fun updateFuelAmounts(lastEditedField: FuelAmountField, value: String) {
        _uiState.update { state ->
            val edited = state.fuelAmountsEdited + lastEditedField
            val liters = if (lastEditedField == FuelAmountField.LITERS) value else state.form.liters
            val pricePerLiter = if (lastEditedField == FuelAmountField.PRICE_PER_LITER) value else state.form.pricePerLiter
            val totalCost = if (lastEditedField == FuelAmountField.TOTAL_COST) value else state.form.totalCost

            val missingField = edited.missingAmountField()
            val computed = when (missingField) {
                FuelAmountField.LITERS -> computeLiters(pricePerLiter, totalCost)
                FuelAmountField.PRICE_PER_LITER -> computePricePerLiterCents(liters, totalCost)
                FuelAmountField.TOTAL_COST -> computeTotalCostFromParts(liters, pricePerLiter)
                null -> null
            }

            var form = state.form.copy(
                liters = liters,
                pricePerLiter = pricePerLiter,
                totalCost = totalCost,
                autoFilledField = when {
                    computed != null -> missingField
                    state.form.autoFilledField != null -> state.form.autoFilledField
                    else -> null
                },
            )
            if (computed != null) {
                form = when (missingField) {
                    FuelAmountField.LITERS -> form.copy(liters = computed.toUserText())
                    FuelAmountField.PRICE_PER_LITER -> form.copy(pricePerLiter = computed.toUserText())
                    FuelAmountField.TOTAL_COST -> form.copy(totalCost = computed.toUserText())
                    null -> form
                }
            } else {
                // Con menos de dos entradas válidas, el campo autocalculado se limpia.
                val stale = form.autoFilledField
                if (stale != null && stale != lastEditedField) {
                    form = when (stale) {
                        FuelAmountField.LITERS -> form.copy(liters = "")
                        FuelAmountField.PRICE_PER_LITER -> form.copy(pricePerLiter = "")
                        FuelAmountField.TOTAL_COST -> form.copy(totalCost = "")
                        else -> form
                    }
                    form = form.copy(autoFilledField = null)
                }
            }

            state.copy(
                form = form,
                fuelAmountsEdited = edited,
                isDirty = true,
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
    /** Amount fields the user has typed into during this session. */
    val fuelAmountsEdited: Set<FuelAmountField> = emptySet(),
    /** True once the user has modified the form (drives the unsaved-changes guard). */
    val isDirty: Boolean = false,
)
