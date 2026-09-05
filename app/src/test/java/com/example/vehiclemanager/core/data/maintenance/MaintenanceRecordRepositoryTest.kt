package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceCategory
import com.example.vehiclemanager.core.domain.MaintenanceRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MaintenanceRecordRepositoryTest {
    @Test
    fun entityMappingPreservesAllFields() {
        val entity = MaintenanceRecordEntity(
            id = 4,
            vehicleId = 8,
            title = "Oil and filter change",
            category = MaintenanceCategory.OIL_CHANGE,
            costCents = 12_500,
            odometerKm = 50_000,
            timestampMs = 1_735_689_600_000,
            notes = "Used synthetic oil",
            performedBy = "North Garage",
        )

        assertEquals(
            MaintenanceRecord(
                id = 4,
                vehicleId = 8,
                title = "Oil and filter change",
                category = MaintenanceCategory.OIL_CHANGE,
                costCents = 12_500,
                odometerKm = 50_000,
                timestampMs = 1_735_689_600_000,
                notes = "Used synthetic oil",
                performedBy = "North Garage",
            ),
            entity.toDomain(),
        )
    }

    @Test
    fun repositoryMapsObservedRecords() = runBlocking {
        val record = MaintenanceRecord(
            id = 1,
            vehicleId = 9,
            title = "Brake inspection",
            category = MaintenanceCategory.BRAKES,
            costCents = 3_000,
            odometerKm = 20_000,
            timestampMs = 100,
            notes = null,
            performedBy = null,
        )
        val repository = MaintenanceRecordRepositoryImpl(
            object : MaintenanceRecordDao {
                override fun observeForVehicle(vehicleId: Long) = flowOf(listOf(record.toEntity()))
                override suspend fun findById(id: Long) = record.toEntity()
                override suspend fun findAll() = listOf(record.toEntity())
                override suspend fun deleteAll() = Unit
                override suspend fun insert(record: MaintenanceRecordEntity) = record.id
                override suspend fun update(record: MaintenanceRecordEntity) = Unit
                override suspend fun delete(record: MaintenanceRecordEntity) = Unit
            },
        )

        assertEquals(listOf(record), repository.observeMaintenanceRecords(9).first())
    }
}
