package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceSchedule
import com.example.vehiclemanager.core.domain.MaintenanceScheduleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: MaintenanceScheduleDao,
) : MaintenanceScheduleRepository {
    override fun observeSchedules(vehicleId: Long): Flow<List<MaintenanceSchedule>> =
        scheduleDao.observeForVehicle(vehicleId).map { entities -> entities.map(MaintenanceScheduleEntity::toDomain) }

    override suspend fun getSchedule(id: Long): MaintenanceSchedule? =
        scheduleDao.findById(id)?.toDomain()

    override suspend fun insertSchedule(schedule: MaintenanceSchedule): Long =
        scheduleDao.insert(schedule.toEntity())

    override suspend fun updateSchedule(schedule: MaintenanceSchedule) {
        scheduleDao.update(schedule.toEntity())
    }

    override suspend fun deleteSchedule(schedule: MaintenanceSchedule) {
        scheduleDao.delete(schedule.toEntity())
    }
}
