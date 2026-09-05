package com.example.vehiclemanager.feature.fuel

import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FuelHistoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun historyIsScopedToActiveVehicleAndConfirmedDeleteRemovesRecord() = runTest {
        val vehicle = vehicle(7)
        val record = record(vehicle.id)
        val records = MutableStateFlow(listOf(record))
        val repository = FakeFuelRecordRepository(records)
        val viewModel = FuelHistoryViewModel(
            activeVehicleRepository = FakeActiveVehicleRepository(vehicle),
            fuelRecordRepository = repository,
        )

        assertEquals(vehicle, viewModel.uiState.first { it.activeVehicle != null }.activeVehicle)
        assertEquals(listOf(record), viewModel.uiState.first { it.records.isNotEmpty() }.records)

        viewModel.requestDelete(record)
        assertEquals(
            record,
            viewModel.uiState.first { it.recordPendingDeletion != null }.recordPendingDeletion,
        )

        viewModel.confirmDelete()

        assertEquals(emptyList<FuelRecord>(), viewModel.uiState.first { it.records.isEmpty() }.records)
        assertEquals(listOf(record), repository.deletedRecords)
    }

    private fun vehicle(id: Long) = Vehicle(
        id = id,
        name = "Vehicle $id",
        make = "Make",
        model = "Model",
        year = 2024,
        licensePlate = "PLATE-$id",
        vin = null,
        fuelType = FuelType.PETROL,
        primaryOdometerKm = 1_000,
    )

    private fun record(vehicleId: Long) = FuelRecord(
        id = 1,
        vehicleId = vehicleId,
        timestampMs = 100,
        odometerKm = 1_100,
        litersX100 = 4_000,
        pricePerLiterCents = 179,
        totalCostCents = 7_160,
        isFullTank = true,
        stationName = "Station",
        notes = null,
    )

    private class FakeActiveVehicleRepository(vehicle: Vehicle) : ActiveVehicleRepository {
        override val activeVehicle: StateFlow<Vehicle?> = MutableStateFlow(vehicle)

        override suspend fun setActiveVehicle(vehicleId: Long) = Unit

        override suspend fun clearActiveVehicle() = Unit
    }

    private class FakeFuelRecordRepository(
        private val records: MutableStateFlow<List<FuelRecord>>,
    ) : FuelRecordRepository {
        val deletedRecords = mutableListOf<FuelRecord>()

        override fun observeFuelRecords(vehicleId: Long): Flow<List<FuelRecord>> = records

        override suspend fun getFuelRecord(id: Long): FuelRecord? = records.value.firstOrNull { it.id == id }

        override suspend fun insertFuelRecord(record: FuelRecord): Long = record.id

        override suspend fun updateFuelRecord(record: FuelRecord) = Unit

        override suspend fun deleteFuelRecord(record: FuelRecord) {
            deletedRecords += record
            records.value = records.value.filterNot { it.id == record.id }
        }
    }
}
