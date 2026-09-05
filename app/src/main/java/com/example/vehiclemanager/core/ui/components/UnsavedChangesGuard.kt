package com.example.vehiclemanager.core.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.vehiclemanager.R

/** Holds whether the unsaved-changes confirmation dialog is visible. */
class UnsavedChangesState {
    var isConfirmationVisible: Boolean by mutableStateOf(false)
        private set

    fun requestConfirmation() {
        isConfirmationVisible = true
    }

    fun dismiss() {
        isConfirmationVisible = false
    }
}

/** Remembers an [UnsavedChangesState] across recompositions. */
@Composable
fun rememberUnsavedChangesState(): UnsavedChangesState = remember { UnsavedChangesState() }

/**
 * Shared guard for form screens: shows the discard-confirmation dialog when
 * requested and intercepts the system back gesture while the form is dirty.
 *
 * In-app navigation (bottom tabs, top bar) is intercepted by the AppScaffold
 * navigation guard, which shares the same [UnsavedChangesState] so both paths
 * lead to the same dialog.
 */
@Composable
fun UnsavedChangesGuard(
    state: UnsavedChangesState,
    isDirty: Boolean,
    onDiscard: () -> Unit,
) {
    if (state.isConfirmationVisible) {
        AlertDialog(
            onDismissRequest = state::dismiss,
            title = { Text(text = stringResource(R.string.unsaved_changes_title)) },
            text = { Text(text = stringResource(R.string.unsaved_changes_message)) },
            confirmButton = {
                Button(onClick = {
                    state.dismiss()
                    onDiscard()
                }) {
                    Text(text = stringResource(R.string.unsaved_changes_discard))
                }
            },
            dismissButton = {
                TextButton(onClick = state::dismiss) {
                    Text(text = stringResource(R.string.unsaved_changes_keep))
                }
            },
        )
    }
    if (isDirty) {
        BackHandler {
            state.requestConfirmation()
        }
    }
}
