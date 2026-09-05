package com.example.vehiclemanager.core.ui.navigation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppScaffoldViewModelTest {
    private val mainDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun dirtyTokensTrackRegistration() = runTest {
        val holder = FormDirtyStateHolder()
        val token = Any()

        holder.setDirty(token, isDirty = true)
        assertEquals(setOf(token), holder.dirtyTokens.value)

        holder.setDirty(token, isDirty = false)
        assertEquals(emptySet<Any>(), holder.dirtyTokens.value)
    }

    @Test
    fun clearAllRemovesEveryRegistration() = runTest {
        val holder = FormDirtyStateHolder()
        val first = Any()
        val second = Any()

        holder.setDirty(first, isDirty = true)
        holder.setDirty(second, isDirty = true)
        holder.clearAll()

        assertEquals(emptySet<Any>(), holder.dirtyTokens.value)
    }

    @Test
    fun navigationIsBlockedWhileAFormIsDirty() = runTest {
        val holder = FormDirtyStateHolder()
        val viewModel = AppScaffoldViewModel(holder)
        val token = Any()
        holder.setDirty(token, isDirty = true)

        var navigated = false
        viewModel.onPrimaryDestinationSelected { navigated = true }

        assertFalse(navigated)
        assertNotNull(viewModel.uiState.value.pendingDestination)
    }

    @Test
    fun confirmingDiscardClearsDirtyStateAndRunsPendingNavigation() = runTest {
        val holder = FormDirtyStateHolder()
        val viewModel = AppScaffoldViewModel(holder)
        val token = Any()
        holder.setDirty(token, isDirty = true)

        var navigated = false
        viewModel.onPrimaryDestinationSelected { navigated = true }
        viewModel.confirmDiscard()

        assertEquals(emptySet<Any>(), holder.dirtyTokens.value)
        assertNull(viewModel.uiState.value.pendingDestination)
        assertTrue(navigated)
    }

    @Test
    fun navigationRunsImmediatelyWhenNoFormIsDirty() = runTest {
        val viewModel = AppScaffoldViewModel(FormDirtyStateHolder())

        var navigated = false
        viewModel.onPrimaryDestinationSelected { navigated = true }

        assertTrue(navigated)
        assertNull(viewModel.uiState.value.pendingDestination)
    }

    @Test
    fun dismissDropsThePendingNavigation() = runTest {
        val holder = FormDirtyStateHolder()
        val viewModel = AppScaffoldViewModel(holder)
        holder.setDirty(Any(), isDirty = true)

        viewModel.onPrimaryDestinationSelected { }
        viewModel.dismiss()

        assertNull(viewModel.uiState.value.pendingDestination)
        // Dirty forms remain registered: the user stayed on the form.
        assertEquals(1, holder.dirtyTokens.value.size)
    }
}
