package com.example.vehiclemanager.core.data.database

import androidx.room.withTransaction
import com.example.vehiclemanager.core.data.fuel.FuelRecordDao
import com.example.vehiclemanager.core.data.fuel.toDomain
import com.example.vehiclemanager.core.data.fuel.toEntity
import com.example.vehiclemanager.core.data.maintenance.MaintenanceRecordDao
import com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleDao
import com.example.vehiclemanager.core.data.maintenance.toDomain
import com.example.vehiclemanager.core.data.maintenance.toEntity
import com.example.vehiclemanager.core.data.vehicle.VehicleDao
import com.example.vehiclemanager.core.data.vehicle.toDomain
import com.example.vehiclemanager.core.data.vehicle.toEntity
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.MaintenanceRecord
import com.example.vehiclemanager.core.domain.MaintenanceSchedule
import com.example.vehiclemanager.core.domain.VehicleBackupRepository
import com.example.vehiclemanager.core.domain.VehicleBackupSnapshot
import javax.inject.Inject

class RoomVehicleBackupRepository @Inject constructor(
    private val database: VehicleDatabase,
    private val vehicleDao: VehicleDao,
    private val fuelRecordDao: FuelRecordDao,
    private val maintenanceRecordDao: MaintenanceRecordDao,
    private val maintenanceScheduleDao: MaintenanceScheduleDao,
    private val activeVehicleRepository: ActiveVehicleRepository,
) : VehicleBackupRepository {
    override suspend fun readSnapshot(): VehicleBackupSnapshot {
        val vehicles = vehicleDao.findAll().map { it.toDomain() }
        val activeVehicleId = activeVehicleRepository.activeVehicle.value?.id
        return VehicleBackupSnapshot(
            activeVehicleId = activeVehicleId,
            vehicles = vehicles,
            fuelRecords = fuelRecordDao.findAll().map { it.toDomain() },
            maintenanceRecords = maintenanceRecordDao.findAll().map { it.toDomain() },
            maintenanceSchedules = maintenanceScheduleDao.findAll().map { it.toDomain() },
        )
    }

    override suspend fun replaceSnapshot(snapshot: VehicleBackupSnapshot) {
        database.withTransaction {
            maintenanceScheduleDao.deleteAll()
            maintenanceRecordDao.deleteAll()
            fuelRecordDao.deleteAll()
            vehicleDao.deleteAll()

            snapshot.vehicles.forEach { vehicleDao.insert(it.toEntity()) }
            snapshot.fuelRecords.forEach { fuelRecordDao.insert(it.toEntity()) }
            snapshot.maintenanceRecords.forEach { maintenanceRecordDao.insert(it.toEntity()) }
            snapshot.maintenanceSchedules.forEach { maintenanceScheduleDao.insert(it.toEntity()) }
        }

        if (snapshot.activeVehicleId == null) {
            activeVehicleRepository.clearActiveVehicle()
        } else {
            activeVehicleRepository.setActiveVehicle(snapshot.activeVehicleId)
        }
    }
}
