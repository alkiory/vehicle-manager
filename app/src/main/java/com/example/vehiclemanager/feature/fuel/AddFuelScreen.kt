package com.example.vehiclemanager.feature.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.core.ui.components.UnsavedChangesGuard
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerPrimaryButton
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import com.example.vehiclemanager.core.ui.components.rememberUnsavedChangesState
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFuelScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddFuelViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val unsavedChangesState = rememberUnsavedChangesState()

    LaunchedEffect(uiState.saveCompleted) {
        if (uiState.saveCompleted) {
            viewModel.consumeSaveCompleted()
            onNavigateBack()
        }
    }

    UnsavedChangesGuard(
        state = unsavedChangesState,
        isDirty = uiState.isDirty && !uiState.isSaving,
        onDiscard = onNavigateBack,
    )

    VehicleManagerScreen(
        topBar = {
            VehicleManagerAppBar(
                title = "Nuevo repostaje",
                actions = {
                    TextButton(onClick = {
                        if (uiState.isDirty) unsavedChangesState.requestConfirmation() else onNavigateBack()
                    }) {
                        Text(text = "Cancelar")
                    }
                },
            )
        },
    ) {
        when {
            uiState.isLoading -> LoadingContent()
            uiState.noActiveVehicle -> NoActiveVehicleContent(onNavigateBack)
            else -> FuelFormContent(
                uiState = uiState,
                onTimestampChanged = viewModel::updateTimestamp,
                onOdometerChanged = viewModel::updateOdometer,
                onLitersChanged = viewModel::updateLiters,
                onPriceChanged = viewModel::updatePricePerLiter,
                onFullTankChanged = viewModel::updateFullTank,
                onStationChanged = viewModel::updateStationName,
                onNotesChanged = viewModel::updateNotes,
                onSave = viewModel::save,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NoActiveVehicleContent(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Añade un vehículo antes de registrar un repostaje.",
            style = MaterialTheme.typography.bodyLarge,
        )
        TextButton(onClick = onNavigateBack) {
            Text(text = "Volver")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelFormContent(
    uiState: AddFuelUiState,
    onTimestampChanged: (Long) -> Unit,
    onOdometerChanged: (String) -> Unit,
    onLitersChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onFullTankChanged: (Boolean) -> Unit,
    onStationChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    val form = uiState.form
    val errors = uiState.errors
    var datePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = uiState.activeVehicle?.let { "Para ${it.name}" } ?: "Vehículo activo",
            style = MaterialTheme.typography.titleMedium,
        )

        OutlinedButton(
            onClick = { datePickerVisible = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = dateFormatter.format(Date(form.timestampMs)))
        }

        FuelTextField(
            value = form.odometerKm,
            onValueChange = onOdometerChanged,
            label = "Odómetro (km)",
            keyboardType = KeyboardType.Number,
            error = errors.odometer != null,
            supportingText = errors.odometer?.fuelValidationMessage("Introduce el odómetro."),
        )
        FuelTextField(
            value = form.liters,
            onValueChange = onLitersChanged,
            label = "Cantidad de combustible (litros)",
            keyboardType = KeyboardType.Decimal,
            error = errors.liters != null,
            supportingText = errors.liters?.fuelValidationMessage("Introduce la cantidad de combustible."),
        )
        FuelTextField(
            value = form.pricePerLiter,
            onValueChange = onPriceChanged,
            label = "Precio por litro",
            keyboardType = KeyboardType.Decimal,
            error = errors.pricePerLiter != null,
            supportingText = errors.pricePerLiter?.fuelValidationMessage("Introduce el precio."),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Depósito lleno", style = MaterialTheme.typography.bodyLarge)
            Switch(checked = form.isFullTank, onCheckedChange = onFullTankChanged)
        }

        FuelTextField(
            value = form.stationName,
            onValueChange = onStationChanged,
            label = "Nombre de la gasolinera (opcional)",
        )
        FuelTextField(
            value = form.notes,
            onValueChange = onNotesChanged,
            label = "Notas (opcional)",
            singleLine = false,
        )

        uiState.previousOdometerKm?.let { previous ->
            Text(
                text = "Odómetro anterior: $previous km",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        uiState.saveError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        VehicleManagerPrimaryButton(
            text = if (uiState.isSaving) "Guardando…" else "Guardar repostaje",
            onClick = onSave,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (datePickerVisible) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = form.timestampMs)
        DatePickerDialog(
            onDismissRequest = { datePickerVisible = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let(onTimestampChanged)
                    datePickerVisible = false
                }) {
                    Text(text = "Seleccionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { datePickerVisible = false }) {
                    Text(text = "Cancelar")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FuelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = error,
        supportingText = supportingText?.let { message ->
            { Text(text = message) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

private fun String.fuelValidationMessage(default: String): String = when (this) {
    "positive" -> "El valor debe ser mayor que cero."
    "lower_than_previous" -> "El odómetro no puede ser menor que el del repostaje anterior."
    "invalid" -> "Introduce un número válido."
    else -> default
}
