package com.example.vehiclemanager.feature.maintenance

import androidx.lifecycle.SavedStateHandle
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.MaintenanceRecord
import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleRepository
import com.example.vehiclemanager.core.ui.navigation.AddEditMaintenanceRoute
import com.example.vehiclemanager.core.ui.navigation.FormDirtyStateHolder
import com.example.vehiclemanager.feature.fuel.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AddMaintenanceViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun savePersistsServiceAndAdvancesVehicleOdometer() = runTest {
        val vehicle = vehicle(10_000)
        val updatedVehicles = mutableListOf<Vehicle>()
        val recordRepository = FakeMaintenanceRepository()
        val viewModel = AddMaintenanceViewModel(
            activeVehicleRepository = FakeActiveVehicleRepository(vehicle),
            maintenanceRecordRepository = recordRepository,
            vehicleRepository = FakeVehicleRepository(updatedVehicles),
            formDirtyStateHolder = FormDirtyStateHolder(),
            savedStateHandle = SavedStateHandle(mapOf("recordId" to null)),
        )

        viewModel.updateTitle("Brake service")
        viewModel.updateCost("250.00")
        viewModel.updateOdometer("10500")
        viewModel.save()

        assertEquals("Brake service", recordRepository.inserted?.title)
        assertEquals(10_500L, updatedVehicles.single().primaryOdometerKm)
    }

    @Test
    fun formEditsMarkTheStateDirtyAndRegisterWithTheHolder() = runTest {
        val formDirtyStateHolder = FormDirtyStateHolder()
        val viewModel = AddMaintenanceViewModel(
            activeVehicleRepository = FakeActiveVehicleRepository(vehicle(10_000)),
            maintenanceRecordRepository = FakeMaintenanceRepository(),
            vehicleRepository = FakeVehicleRepository(mutableListOf()),
            formDirtyStateHolder = formDirtyStateHolder,
            savedStateHandle = SavedStateHandle(mapOf("recordId" to null)),
        )

        viewModel.updateTitle("Oil change")

        assertEquals(true, viewModel.uiState.value.isDirty)
        assertEquals(1, formDirtyStateHolder.dirtyTokens.value.size)
    }

    private fun vehicle(odometerKm: Long) = Vehicle(
        id = 1,
        name = "Test vehicle",
        make = "Make",
        model = "Model",
        year = 2024,
        licensePlate = "TEST",
        vin = null,
        fuelType = FuelType.PETROL,
        primaryOdometerKm = odometerKm,
    )

    private class FakeActiveVehicleRepository(vehicle: Vehicle) : ActiveVehicleRepository {
        override val activeVehicle: StateFlow<Vehicle?> = MutableStateFlow(vehicle)
        override suspend fun setActiveVehicle(vehicleId: Long) = Unit
        override suspend fun clearActiveVehicle() = Unit
    }

    private class FakeMaintenanceRepository : MaintenanceRecordRepository {
        var inserted: MaintenanceRecord? = null
        override fun observeMaintenanceRecords(vehicleId: Long): Flow<List<MaintenanceRecord>> = MutableStateFlow(emptyList())
        override suspend fun getMaintenanceRecord(id: Long): MaintenanceRecord? = null
        override suspend fun insertMaintenanceRecord(record: MaintenanceRecord): Long {
            inserted = record
            return 1
        }
        override suspend fun updateMaintenanceRecord(record: MaintenanceRecord) = Unit
        override suspend fun deleteMaintenanceRecord(record: MaintenanceRecord) = Unit
    }

    private class FakeVehicleRepository(
        private val updatedVehicles: MutableList<Vehicle>,
    ) : VehicleRepository {
        override val vehicles: Flow<List<Vehicle>> = MutableStateFlow(emptyList())
        override suspend fun getVehicle(id: Long): Vehicle? = null
        override suspend fun insertVehicle(vehicle: Vehicle): Long = vehicle.id
        override suspend fun updateVehicle(vehicle: Vehicle) { updatedVehicles += vehicle }
        override suspend fun deleteVehicle(vehicle: Vehicle) = Unit
    }
}
