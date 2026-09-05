package com.example.vehiclemanager.core.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppScaffoldViewModel @Inject constructor(
    private val formDirtyStateHolder: FormDirtyStateHolder,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppScaffoldUiState())
    val uiState: StateFlow<AppScaffoldUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            formDirtyStateHolder.dirtyTokens.collect { tokens ->
                _uiState.update { it.copy(hasDirtyForms = tokens.isNotEmpty()) }
            }
        }
    }
    /**
     * Called when the user confirms leaving the form: clears every dirty
     * registration and runs the navigation request that was pending.
     */
    fun confirmDiscard() {
        val navigate = _uiState.value.pendingDestination
        formDirtyStateHolder.clearAll()
        _uiState.update { it.copy(hasDirtyForms = false, pendingDestination = null) }
        navigate?.invoke()
    }

    fun dismiss() {
        _uiState.update { it.copy(pendingDestination = null) }
    }

    /**
     * Gate for primary-destination navigation: while a form has unsaved changes,
     * navigation is suspended and the confirmation dialog is shown instead.
     */
    fun onPrimaryDestinationSelected(requestNavigation: () -> Unit) {
        if (_uiState.value.hasDirtyForms) {
            _uiState.update { it.copy(pendingDestination = requestNavigation) }
        } else {
            requestNavigation()
        }
    }
}

data class AppScaffoldUiState(
    val hasDirtyForms: Boolean = false,
    val pendingDestination: (() -> Unit)? = null,
)
