package com.example.vehiclemanager.feature.fuel

import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleRepository
import com.example.vehiclemanager.core.ui.navigation.FormDirtyStateHolder
import com.example.vehiclemanager.feature.fuel.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddFuelViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dirtyStateHolder = FormDirtyStateHolder()

    @Test
    fun computesTotalFromLitersAndPrice() = runTest {
        val viewModel = createViewModel()

        viewModel.updateLiters("45.25")
        viewModel.updatePricePerLiter("1.79")

        // 45.25 L x 1.79 €/L = 80.9975 € → 81.00 (HALF_UP)
        assertEquals("81.00", viewModel.uiState.value.form.totalCost)
        assertEquals(FuelAmountField.TOTAL_COST, viewModel.uiState.value.form.autoFilledField)
    }

    @Test
    fun computesPriceFromLitersAndTotal() = runTest {
        val viewModel = createViewModel()

        viewModel.updateLiters("45.25")
        viewModel.updateTotalCost("81.00")

        assertEquals("1.79", viewModel.uiState.value.form.pricePerLiter)
        assertEquals(FuelAmountField.PRICE_PER_LITER, viewModel.uiState.value.form.autoFilledField)
    }

    @Test
    fun computesLitersFromPriceAndTotal() = runTest {
        val viewModel = createViewModel()

        viewModel.updatePricePerLiter("1.79")
        viewModel.updateTotalCost("80.99")

        assertEquals("45.25", viewModel.uiState.value.form.liters)
        assertEquals(FuelAmountField.LITERS, viewModel.uiState.value.form.autoFilledField)
    }

    @Test
    fun autoFilledValueRefreshesWhenAnInputChanges() = runTest {
        val viewModel = createViewModel()

        viewModel.updateLiters("45.25")
        viewModel.updatePricePerLiter("1.79")
        assertEquals("81.00", viewModel.uiState.value.form.totalCost)

        viewModel.updateLiters("50")
        assertEquals("89.50", viewModel.uiState.value.form.totalCost)
    }

    @Test
    fun autoFilledFieldClearsWhenAnInputBecomesInvalid() = runTest {
        val viewModel = createViewModel()

        viewModel.updateLiters("45.25")
        viewModel.updatePricePerLiter("1.79")
        assertEquals("81.00", viewModel.uiState.value.form.totalCost)

        viewModel.updatePricePerLiter("bad")
        assertEquals("", viewModel.uiState.value.form.totalCost)
        assertEquals(null, viewModel.uiState.value.form.autoFilledField)
    }

    @Test
    fun autoFillDoesNotOverwriteTheFieldTheUserIsTyping() = runTest {
        val viewModel = createViewModel()

        viewModel.updateLiters("45.25")
        viewModel.updatePricePerLiter("1.79")
        // The user rewrites total cost by hand: it must not be recomputed.
        viewModel.updateTotalCost("90.00")
        assertEquals("90.00", viewModel.uiState.value.form.totalCost)
    }

    @Test
    fun savingUsesTheAutoFilledTotalCost() = runTest {
        val recordRepository = FakeFuelRecordRepository()
        val viewModel = createViewModel(recordRepository = recordRepository)
        val vehicle = vehicle()

        viewModel.updateOdometer(vehicle.primaryOdometerKm.toString())
        viewModel.updateLiters("45.25")
        viewModel.updatePricePerLiter("1.79")
        viewModel.save()
        advanceUntilIdle()

        val inserted = recordRepository.inserted
        assertTrue(inserted != null)
        assertEquals(8_100L, inserted?.totalCostCents)
        assertEquals(4_525, inserted?.litersX100)
        assertEquals(179L, inserted?.pricePerLiterCents)
    }

    @Test
    fun formEditsMarkTheStateDirty() = runTest {
        val viewModel = createViewModel()

        assertFalse(viewModel.uiState.value.isDirty)
        viewModel.updateLiters("10")
        assertTrue(viewModel.uiState.value.isDirty)
    }

    private fun createViewModel(
        recordRepository: FakeFuelRecordRepository = FakeFuelRecordRepository(),
    ): AddFuelViewModel = AddFuelViewModel(
        activeVehicleRepository = FakeActiveVehicleRepository(vehicle()),
        fuelRecordRepository = recordRepository,
        vehicleRepository = FakeVehicleRepository(),
        formDirtyStateHolder = dirtyStateHolder,
    )

    private fun vehicle() = Vehicle(
        id = 1,
        name = "Test vehicle",
        make = "Make",
        model = "Model",
        year = 2024,
        licensePlate = "TEST",
        vin = null,
        fuelType = FuelType.PETROL,
        primaryOdometerKm = 10_000,
    )

    private class FakeActiveVehicleRepository(
        vehicle: Vehicle,
    ) : ActiveVehicleRepository {
        override val activeVehicle: StateFlow<Vehicle?> = MutableStateFlow(vehicle)
        override suspend fun setActiveVehicle(vehicleId: Long) = Unit
        override suspend fun clearActiveVehicle() = Unit
    }

    private class FakeFuelRecordRepository : FuelRecordRepository {
        var inserted: FuelRecord? = null
        override fun observeFuelRecords(vehicleId: Long): Flow<List<FuelRecord>> =
            MutableStateFlow(emptyList())
        override fun observeRecentPrices(): Flow<List<FuelRecord>> =
            MutableStateFlow(emptyList())

        override suspend fun getFuelRecord(id: Long): FuelRecord? = null
        override suspend fun insertFuelRecord(record: FuelRecord): Long {
            inserted = record
            return 1
        }
        override suspend fun updateFuelRecord(record: FuelRecord) = Unit
        override suspend fun deleteFuelRecord(record: FuelRecord) = Unit
    }

    private class FakeVehicleRepository : VehicleRepository {
        override val vehicles: Flow<List<Vehicle>> = MutableStateFlow(emptyList())
        override suspend fun getVehicle(id: Long): Vehicle? = null
        override suspend fun insertVehicle(vehicle: Vehicle): Long = 1
        override suspend fun updateVehicle(vehicle: Vehicle) = Unit
        override suspend fun deleteVehicle(vehicle: Vehicle) = Unit
    }
}
