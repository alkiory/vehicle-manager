package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceSchedule
import com.example.vehiclemanager.core.domain.MaintenanceScheduleRepository
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MaintenanceScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: MaintenanceScheduleDao,
    private val activeVehicleRepository: ActiveVehicleRepository,
) : MaintenanceScheduleRepository {
    override fun observeSchedules(vehicleId: Long): Flow<List<MaintenanceSchedule>> =
        scheduleDao.observeForVehicle(vehicleId).map { entities -> entities.map(MaintenanceScheduleEntity::toDomain) }

    override fun observeSchedulesForActiveVehicle(): Flow<List<MaintenanceSchedule>> =
        activeVehicleRepository.activeVehicle.combine(scheduleDao.observeAll()) { activeVehicle: com.example.vehiclemanager.core.domain.Vehicle?, schedules: List<com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleEntity> ->
            val vehicleId = activeVehicle?.id ?: return@combine emptyList()
            schedules.filter { it.vehicleId == vehicleId }.map(MaintenanceScheduleEntity::toDomain)
        }

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
