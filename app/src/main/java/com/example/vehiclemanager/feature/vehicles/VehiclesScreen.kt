package com.example.vehiclemanager.feature.vehicles

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vehiclemanager.R
import com.example.vehiclemanager.core.ui.components.VehicleManagerAppBar
import com.example.vehiclemanager.core.ui.components.VehicleManagerScreen
import kotlinx.coroutines.launch

@Composable
fun VehiclesScreen(
    onAddVehicle: () -> Unit = {},
    viewModel: VehicleBackupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        val json = uiState.exportJson
        if (uri != null && json != null) {
            scope.launch {
                writeBackup(context, uri, json)
                viewModel.consumeExportJson()
            }
        }
    }
    val openBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val json = readBackup(context, uri)
                if (json == null) {
                    viewModel.importBackup("invalid")
                } else {
                    viewModel.importBackup(json)
                }
            }
        }
    }

    LaunchedEffect(uiState.exportJson) {
        if (uiState.exportJson != null) {
            createBackupLauncher.launch(context.getString(R.string.backup_export_filename))
        }
    }

    VehicleManagerScreen(
        topBar = { VehicleManagerAppBar(title = stringResource(R.string.vehicles_title)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddVehicle,
                modifier = Modifier.semantics {
                    contentDescription = context.getString(R.string.add_vehicle_content_description)
                },
            ) { Text(text = "+") }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Text(text = stringResource(R.string.vehicles_title), style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(R.string.vehicles_description),
                style = MaterialTheme.typography.bodyLarge,
            )
            BackupCard(
                isBusy = uiState.isBusy,
                onExport = viewModel::exportBackup,
                onImport = { openBackupLauncher.launch(arrayOf("application/json", "text/json")) },
            )
            when {
                uiState.importCompleted -> Text(
                    text = stringResource(R.string.backup_imported),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                uiState.error != null -> Text(
                    text = stringResource(R.string.backup_error, uiState.error.orEmpty()),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun BackupCard(
    isBusy: Boolean,
    onExport: () -> Unit,
    onImport: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = stringResource(R.string.backup_title), style = MaterialTheme.typography.titleLarge)
        Text(text = stringResource(R.string.backup_description), style = MaterialTheme.typography.bodyMedium)
        if (isBusy) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = stringResource(R.string.backup_working),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else {
            Button(
                onClick = onExport,
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = "Export vehicle data backup"
                },
            ) { Text(text = stringResource(R.string.export_backup)) }
            OutlinedButton(
                onClick = onImport,
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = "Import vehicle data backup"
                },
            ) { Text(text = stringResource(R.string.import_backup)) }
        }
    }
}

private fun writeBackup(context: Context, uri: Uri, json: String) {
    context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
        writer.write(json)
    }
}

private fun readBackup(context: Context, uri: Uri): String? = runCatching {
    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
}.getOrNull()
