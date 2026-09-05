package com.example.vehiclemanager.feature.vehicles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerPrimaryButton
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen

@Composable
fun AddEditVehicleScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditVehicleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveCompleted) {
        if (uiState.saveCompleted) {
            viewModel.consumeSaveCompleted()
            onNavigateBack()
        }
    }

    VehicleManagerScreen(
        topBar = {
            VehicleManagerAppBar(
                title = if (uiState.isEditing) "Editar vehículo" else "Nuevo vehículo",
                actions = {
                    TextButton(onClick = onNavigateBack) {
                        Text(text = "Cancelar")
                    }
                },
            )
        },
    ) {
        when {
            uiState.isLoading -> LoadingContent()
            uiState.loadError != null -> ErrorContent(
                message = "No se pudo cargar el vehículo.",
                onNavigateBack = onNavigateBack,
            )
            else -> VehicleFormContent(
                uiState = uiState,
                onNameChanged = viewModel::updateName,
                onMakeChanged = viewModel::updateMake,
                onModelChanged = viewModel::updateModel,
                onYearChanged = viewModel::updateYear,
                onLicensePlateChanged = viewModel::updateLicensePlate,
                onVinChanged = viewModel::updateVin,
                onFuelTypeChanged = viewModel::updateFuelType,
                onOdometerChanged = viewModel::updateOdometer,
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
private fun ErrorContent(
    message: String,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = onNavigateBack) {
            Text(text = "Volver")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleFormContent(
    uiState: AddEditVehicleUiState,
    onNameChanged: (String) -> Unit,
    onMakeChanged: (String) -> Unit,
    onModelChanged: (String) -> Unit,
    onYearChanged: (String) -> Unit,
    onLicensePlateChanged: (String) -> Unit,
    onVinChanged: (String) -> Unit,
    onFuelTypeChanged: (FuelType) -> Unit,
    onOdometerChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    val form = uiState.form
    val errors = uiState.errors
    var fuelMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VehicleTextField(
            value = form.name,
            onValueChange = onNameChanged,
            label = "Nombre del vehículo",
            error = errors.name != null,
            supportingText = errors.name?.validationMessage("Introduce un nombre de vehículo."),
        )
        VehicleTextField(
            value = form.make,
            onValueChange = onMakeChanged,
            label = "Marca",
            error = errors.make != null,
            supportingText = errors.make?.validationMessage("Introduce la marca del vehículo."),
        )
        VehicleTextField(
            value = form.model,
            onValueChange = onModelChanged,
            label = "Modelo",
            error = errors.model != null,
            supportingText = errors.model?.validationMessage("Introduce el modelo del vehículo."),
        )
        VehicleTextField(
            value = form.year,
            onValueChange = onYearChanged,
            label = "Año",
            keyboardType = KeyboardType.Number,
            error = errors.year != null,
            supportingText = errors.year?.validationMessage("Introduce un año de vehículo válido."),
        )
        VehicleTextField(
            value = form.licensePlate,
            onValueChange = onLicensePlateChanged,
            label = "Matrícula",
            error = errors.licensePlate != null,
            supportingText = errors.licensePlate?.validationMessage("Introduce una matrícula."),
        )
        VehicleTextField(
            value = form.vin,
            onValueChange = onVinChanged,
            label = "VIN (opcional)",
        )

        ExposedDropdownMenuBox(
            expanded = fuelMenuExpanded,
            onExpandedChange = { fuelMenuExpanded = !fuelMenuExpanded },
        ) {
            OutlinedTextField(
                value = form.fuelType.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Tipo de combustible") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelMenuExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = fuelMenuExpanded,
                onDismissRequest = { fuelMenuExpanded = false },
            ) {
                FuelType.entries.forEach { fuelType ->
                    DropdownMenuItem(
                        text = { Text(text = fuelType.displayName) },
                        onClick = {
                            onFuelTypeChanged(fuelType)
                            fuelMenuExpanded = false
                        },
                    )
                }
            }
        }

        VehicleTextField(
            value = form.primaryOdometerKm,
            onValueChange = onOdometerChanged,
            label = "Odómetro (km)",
            keyboardType = KeyboardType.Number,
            error = errors.odometer != null,
            supportingText = errors.odometer?.validationMessage("Introduce un odómetro mayor que cero."),
        )

        uiState.saveError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        VehicleManagerPrimaryButton(
            text = if (uiState.isSaving) "Guardando…" else "Guardar vehículo",
            onClick = onSave,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun VehicleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = error,
        supportingText = supportingText?.let { message ->
            { Text(text = message) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

private fun String.validationMessage(default: String): String = when (this) {
    "positive" -> "El valor debe ser mayor que cero."
    "range" -> "Introduce un año de vehículo realista."
    "invalid" -> "Introduce un número válido."
    else -> default
}

private val FuelType.displayName: String
    get() = name.lowercase().replaceFirstChar(Char::uppercase)
