package com.example.vehiclemanager.feature.maintenance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.core.domain.MaintenanceCategory
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerPrimaryButton
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaintenanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddMaintenanceViewModel = hiltViewModel(),
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
                title = if (uiState.isEditing) "Edit service" else "Add service",
                actions = {
                    TextButton(onClick = onNavigateBack) { Text(text = "Cancel") }
                },
            )
        },
    ) {
        when {
            uiState.isLoading -> BoxedLoadingContent()
            uiState.loadError != null -> FormErrorContent(
                message = "Unable to load service record.",
                onNavigateBack = onNavigateBack,
            )
            uiState.noActiveVehicle -> FormErrorContent(
                message = "Add a vehicle before logging service.",
                onNavigateBack = onNavigateBack,
            )
            else -> MaintenanceFormContent(
                uiState = uiState,
                onTitleChanged = viewModel::updateTitle,
                onCategoryChanged = viewModel::updateCategory,
                onCostChanged = viewModel::updateCost,
                onOdometerChanged = viewModel::updateOdometer,
                onTimestampChanged = viewModel::updateTimestamp,
                onNotesChanged = viewModel::updateNotes,
                onPerformedByChanged = viewModel::updatePerformedBy,
                onSave = viewModel::save,
            )
        }
    }
}

@Composable
private fun BoxedLoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) { CircularProgressIndicator() }
}

@Composable
private fun FormErrorContent(message: String, onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = onNavigateBack) { Text(text = "Go back") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaintenanceFormContent(
    uiState: AddMaintenanceUiState,
    onTitleChanged: (String) -> Unit,
    onCategoryChanged: (MaintenanceCategory) -> Unit,
    onCostChanged: (String) -> Unit,
    onOdometerChanged: (String) -> Unit,
    onTimestampChanged: (Long) -> Unit,
    onNotesChanged: (String) -> Unit,
    onPerformedByChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    val form = uiState.form
    val errors = uiState.errors
    var categoryExpanded by remember { mutableStateOf(false) }
    var datePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = uiState.activeVehicle?.let { "For ${it.name}" } ?: "Active vehicle", style = MaterialTheme.typography.titleMedium)
        MaintenanceTextField(
            value = form.title,
            onValueChange = onTitleChanged,
            label = "Service title",
            error = errors.title != null,
            supportingText = errors.title?.maintenanceMessage("Enter a service title."),
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded },
        ) {
            OutlinedTextField(
                value = form.category.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false },
            ) {
                MaintenanceCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category.displayName) },
                        onClick = { onCategoryChanged(category); categoryExpanded = false },
                    )
                }
            }
        }

        MaintenanceTextField(
            value = form.cost,
            onValueChange = onCostChanged,
            label = "Cost",
            keyboardType = KeyboardType.Decimal,
            error = errors.cost != null,
            supportingText = errors.cost?.maintenanceMessage("Enter a valid cost."),
        )
        MaintenanceTextField(
            value = form.odometerKm,
            onValueChange = onOdometerChanged,
            label = "Odometer (km)",
            keyboardType = KeyboardType.Number,
            error = errors.odometer != null,
            supportingText = errors.odometer?.maintenanceMessage("Enter the service odometer."),
        )

        OutlinedButton(onClick = { datePickerVisible = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = dateFormatter.format(Date(form.timestampMs)))
        }
        MaintenanceTextField(
            value = form.performedBy,
            onValueChange = onPerformedByChanged,
            label = "Performed by (optional)",
        )
        MaintenanceTextField(
            value = form.notes,
            onValueChange = onNotesChanged,
            label = "Notes (optional)",
            singleLine = false,
        )

        uiState.previousOdometerKm?.let { Text(text = "Current odometer: $it km", style = MaterialTheme.typography.bodySmall) }
        uiState.saveError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        VehicleManagerPrimaryButton(
            text = if (uiState.isSaving) "Saving…" else "Save service",
            onClick = onSave,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (datePickerVisible) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = form.timestampMs)
        DatePickerDialog(
            onDismissRequest = { datePickerVisible = false },
            confirmButton = {
                Button(onClick = { pickerState.selectedDateMillis?.let(onTimestampChanged); datePickerVisible = false }) { Text(text = "Select") }
            },
            dismissButton = { TextButton(onClick = { datePickerVisible = false }) { Text(text = "Cancel") } },
        ) { DatePicker(state = pickerState) }
    }
}

@Composable
private fun MaintenanceTextField(
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
        supportingText = supportingText?.let { { Text(text = it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

private fun String.maintenanceMessage(default: String): String = when (this) {
    "positive" -> "Value must be greater than zero."
    "lower_than_previous" -> "Odometer cannot be lower than the previous service."
    "invalid" -> "Enter a valid number."
    else -> default
}

private val MaintenanceCategory.displayName: String
    get() = name.lowercase().split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
