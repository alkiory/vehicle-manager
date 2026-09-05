package com.example.vehiclemanager.core.data.fuel

import com.example.vehiclemanager.core.domain.FuelRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FuelRecordRepositoryTest {
    @Test
    fun entityMappingPreservesIntegerUnitsAndOptionalFields() {
        val entity = FuelRecordEntity(
            id = 3,
            vehicleId = 7,
            timestampMs = 1_735_689_600_000,
            odometerKm = 42_500,
            litersX100 = 4_525,
            pricePerLiterCents = 179,
            totalCostCents = 8_102,
            isFullTank = true,
            stationName = "Central Station",
            notes = "Receipt saved",
        )

        assertEquals(
            FuelRecord(
                id = 3,
                vehicleId = 7,
                timestampMs = 1_735_689_600_000,
                odometerKm = 42_500,
                litersX100 = 4_525,
                pricePerLiterCents = 179,
                totalCostCents = 8_102,
                isFullTank = true,
                stationName = "Central Station",
                notes = "Receipt saved",
            ),
            entity.toDomain(),
        )
    }

    @Test
    fun repositoryMapsVehicleScopedFlow() = runBlocking {
        val record = FuelRecord(
            id = 1,
            vehicleId = 12,
            timestampMs = 100,
            odometerKm = 1_000,
            litersX100 = 3_000,
            pricePerLiterCents = 180,
            totalCostCents = 5_400,
            isFullTank = false,
            stationName = null,
            notes = null,
        )
        val repository = FuelRecordRepositoryImpl(
            object : FuelRecordDao {
                override fun observeForVehicle(vehicleId: Long) = flowOf(listOf(record.toEntity()))
                override suspend fun findById(id: Long) = record.toEntity()
                override suspend fun insert(record: FuelRecordEntity) = record.id
                override suspend fun update(record: FuelRecordEntity) = Unit
                override suspend fun delete(record: FuelRecordEntity) = Unit
            },
        )

        assertEquals(listOf(record), repository.observeFuelRecords(12).first())
    }
}
