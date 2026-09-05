package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceRecord
import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceRecordRepositoryImpl @Inject constructor(
    private val maintenanceRecordDao: MaintenanceRecordDao,
) : MaintenanceRecordRepository {
    override fun observeMaintenanceRecords(vehicleId: Long): Flow<List<MaintenanceRecord>> =
        maintenanceRecordDao.observeForVehicle(vehicleId)
            .map { entities -> entities.map(MaintenanceRecordEntity::toDomain) }

    override suspend fun getMaintenanceRecord(id: Long): MaintenanceRecord? =
        maintenanceRecordDao.findById(id)?.toDomain()

    override suspend fun insertMaintenanceRecord(record: MaintenanceRecord): Long =
        maintenanceRecordDao.insert(record.toEntity())

    override suspend fun updateMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceRecordDao.update(record.toEntity())
    }

    override suspend fun deleteMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceRecordDao.delete(record.toEntity())
    }
}
