package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseBackupUseCasesTest {
    @Test
    fun exportAndImportRoundTripPreservesSnapshot() = runBlocking {
        val snapshot = snapshot()
        val source = FakeBackupRepository(snapshot)
        val exported = ExportDatabaseUseCase(source)()
        val destination = FakeBackupRepository(VehicleBackupSnapshot(null, emptyList(), emptyList(), emptyList(), emptyList()))

        ImportDatabaseUseCase(destination)(exported)

        assertEquals(snapshot, destination.snapshot)
        assertTrue(exported.contains("\"schemaVersion\":1"))
        assertTrue(exported.contains("\"activeVehicleId\":7"))
    }

    @Test
    fun invalidReferenceIsRejectedBeforeRepositoryMutation() = runBlocking {
        val source = FakeBackupRepository(snapshot())
        val exported = ExportDatabaseUseCase(source)()
            .replace("\"vehicleId\":7", "\"vehicleId\":999")
        val destination = FakeBackupRepository(snapshot())

        val error = runCatching { ImportDatabaseUseCase(destination)(exported) }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
        assertEquals(snapshot(), destination.snapshot)
    }

    private fun snapshot() = VehicleBackupSnapshot(
        activeVehicleId = 7,
        vehicles = listOf(
            Vehicle(
                id = 7,
                name = "Daily car",
                make = "Make",
                model = "Model",
                year = 2024,
                licensePlate = "TEST",
                vin = null,
                fuelType = FuelType.PETROL,
                primaryOdometerKm = 12_000,
            ),
        ),
        fuelRecords = listOf(
            FuelRecord(
                id = 11,
                vehicleId = 7,
                timestampMs = 100,
                odometerKm = 11_900,
                litersX100 = 4_000,
                pricePerLiterCents = 180,
                totalCostCents = 7_200,
                isFullTank = true,
                stationName = "Station",
                notes = null,
            ),
        ),
        maintenanceRecords = listOf(
            MaintenanceRecord(
                id = 12,
                vehicleId = 7,
                title = "Oil change",
                category = MaintenanceCategory.OIL_CHANGE,
                costCents = 10_000,
                odometerKm = 11_500,
                timestampMs = 200,
                notes = null,
                performedBy = "Garage",
            ),
        ),
        maintenanceSchedules = listOf(
            MaintenanceSchedule(
                id = 13,
                vehicleId = 7,
                serviceTitle = "Oil change",
                intervalKm = 10_000,
                intervalMonths = 12,
                lastPerformedKm = 11_500,
                lastPerformedDateMs = 200,
            ),
        ),
    )

    private class FakeBackupRepository(
        var snapshot: VehicleBackupSnapshot,
    ) : VehicleBackupRepository {
        override suspend fun readSnapshot(): VehicleBackupSnapshot = snapshot

        override suspend fun replaceSnapshot(snapshot: VehicleBackupSnapshot) {
            this.snapshot = snapshot
        }
    }
}
