package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceSchedule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MaintenanceScheduleRepositoryTest {
    @Test
    fun entityMappingPreservesNullableIntervals() {
        val schedule = MaintenanceSchedule(
            id = 4,
            vehicleId = 8,
            serviceTitle = "Oil change",
            intervalKm = 10_000,
            intervalMonths = null,
            lastPerformedKm = 42_000,
            lastPerformedDateMs = null,
        )

        assertEquals(schedule, schedule.toEntity().toDomain())
    }

    @Test
    fun repositoryMapsVehicleScopedScheduleFlow() = runBlocking {
        val schedule = MaintenanceSchedule(
            id = 2,
            vehicleId = 12,
            serviceTitle = "Inspection",
            intervalKm = null,
            intervalMonths = 12,
            lastPerformedKm = null,
            lastPerformedDateMs = 100,
        )
        val repository = MaintenanceScheduleRepositoryImpl(
            object : MaintenanceScheduleDao {
                override fun observeForVehicle(vehicleId: Long) = flowOf(listOf(schedule.toEntity()))
                override suspend fun findById(id: Long) = schedule.toEntity()
                override suspend fun insert(schedule: MaintenanceScheduleEntity) = schedule.id
                override suspend fun update(schedule: MaintenanceScheduleEntity) = Unit
                override suspend fun delete(schedule: MaintenanceScheduleEntity) = Unit
            },
        )

        assertEquals(listOf(schedule), repository.observeSchedules(12).first())
    }
}
