package com.example.vehiclemanager.core.domain

data class MaintenanceSchedule(
    val id: Long = 0,
    val vehicleId: Long,
    val serviceTitle: String,
    val intervalKm: Long?,
    val intervalMonths: Int?,
    val lastPerformedKm: Long?,
    val lastPerformedDateMs: Long?,
)

interface MaintenanceScheduleRepository {
    fun observeSchedules(vehicleId: Long): kotlinx.coroutines.flow.Flow<List<MaintenanceSchedule>>

    /**
     * Observe schedules for the currently active vehicle.
     */
    fun observeSchedulesForActiveVehicle(): kotlinx.coroutines.flow.Flow<List<MaintenanceSchedule>>

    suspend fun getSchedule(id: Long): MaintenanceSchedule?

    suspend fun insertSchedule(schedule: MaintenanceSchedule): Long

    suspend fun updateSchedule(schedule: MaintenanceSchedule)

    suspend fun deleteSchedule(schedule: MaintenanceSchedule)
}
