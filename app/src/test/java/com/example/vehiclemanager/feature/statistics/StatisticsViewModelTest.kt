package com.example.vehiclemanager.feature.statistics

import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.MaintenanceRecord
import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository
import com.example.vehiclemanager.core.domain.StatsPeriod
import com.example.vehiclemanager.core.domain.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StatisticsViewModelTest {
    @get:Rule
    val mainDispatcherRule = com.example.vehiclemanager.feature.fuel.MainDispatcherRule()

    @Test
    fun selectingPeriodRecalculatesReactiveStatisticsState() = runTest {
        val fuelRecords = MutableStateFlow(
            listOf(
                fuel(timestampMs = 1_000, totalCostCents = 1_000),
                fuel(timestampMs = 2_000, totalCostCents = 2_000),
            ),
        )
        val viewModel = StatisticsViewModel(
            activeVehicleRepository = FakeActiveVehicleRepository(vehicle()),
            fuelRecordRepository = FakeFuelRecordRepository(fuelRecords),
            maintenanceRecordRepository = FakeMaintenanceRecordRepository(),
            calculateVehicleStats = com.example.vehiclemanager.core.domain.CalculateVehicleStatsUseCase(),
        )

        assertEquals(
            StatsPeriod.ALL_TIME,
            viewModel.uiState.first { it.activeVehicle != null }.selectedPeriod,
        )

        viewModel.selectPeriod(StatsPeriod.LAST_30_DAYS)

        assertEquals(
            StatsPeriod.LAST_30_DAYS,
            viewModel.uiState.first { it.selectedPeriod == StatsPeriod.LAST_30_DAYS }.selectedPeriod,
        )
    }

    private fun vehicle() = Vehicle(
        id = 1,
        name = "Test vehicle",
        make = "Make",
        model = "Model",
        year = 2024,
        licensePlate = "TEST",
        vin = null,
        fuelType = FuelType.PETROL,
        primaryOdometerKm = 1_000,
    )

    private fun fuel(timestampMs: Long, totalCostCents: Long) = FuelRecord(
        id = timestampMs,
        vehicleId = 1,
        timestampMs = timestampMs,
        odometerKm = 1_000,
        litersX100 = 300,
        pricePerLiterCents = 180,
        totalCostCents = totalCostCents,
        isFullTank = true,
        stationName = null,
        notes = null,
    )

    private class FakeActiveVehicleRepository(vehicle: Vehicle) : ActiveVehicleRepository {
        override val activeVehicle: StateFlow<Vehicle?> = MutableStateFlow(vehicle)

        override suspend fun setActiveVehicle(vehicleId: Long) = Unit

        override suspend fun clearActiveVehicle() = Unit
    }

    private class FakeFuelRecordRepository(
        private val records: Flow<List<FuelRecord>>,
    ) : FuelRecordRepository {
        override fun observeFuelRecords(vehicleId: Long): Flow<List<FuelRecord>> = records
        override fun observeRecentPrices(): Flow<List<FuelRecord>> = records

        override suspend fun getFuelRecord(id: Long): FuelRecord? = null

        override suspend fun insertFuelRecord(record: FuelRecord): Long = record.id

        override suspend fun updateFuelRecord(record: FuelRecord) = Unit

        override suspend fun deleteFuelRecord(record: FuelRecord) = Unit
    }

    private class FakeMaintenanceRecordRepository : MaintenanceRecordRepository {
        override fun observeMaintenanceRecords(vehicleId: Long): Flow<List<MaintenanceRecord>> =
            MutableStateFlow(emptyList())

        override suspend fun getMaintenanceRecord(id: Long): MaintenanceRecord? = null

        override suspend fun insertMaintenanceRecord(record: MaintenanceRecord): Long = record.id

        override suspend fun updateMaintenanceRecord(record: MaintenanceRecord) = Unit

        override suspend fun deleteMaintenanceRecord(record: MaintenanceRecord) = Unit
    }
}
