package com.example.vehiclemanager.core.ui.navigation

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Process-wide registry of form ViewModels with unsaved changes.
 *
 * Form ViewModels register a unique token and toggle their dirty flag here;
 * [AppScaffoldViewModel] reads it to intercept bottom-tab navigation while a
 * form is dirty and ask for confirmation before discarding the changes.
 */
@Singleton
class FormDirtyStateHolder @Inject constructor() {
    private val _dirtyTokens = MutableStateFlow<Set<Any>>(emptySet())

    /** Tokens of form ViewModels currently holding unsaved user input. */
    val dirtyTokens: StateFlow<Set<Any>> = _dirtyTokens.asStateFlow()

    fun setDirty(token: Any, isDirty: Boolean) {
        _dirtyTokens.update { current ->
            if (isDirty) current + token else current - token
        }
    }

    /** Clears every registration; used when the user confirms discarding. */
    fun clearAll() {
        _dirtyTokens.update { emptySet() }
    }
}
