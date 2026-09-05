package com.example.vehiclemanager.core.data.fuel

import com.example.vehiclemanager.core.domain.FuelRecord
import com.example.vehiclemanager.core.domain.FuelRecordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FuelRecordRepositoryImpl @Inject constructor(
    private val fuelRecordDao: FuelRecordDao,
) : FuelRecordRepository {
    override fun observeFuelRecords(vehicleId: Long): Flow<List<FuelRecord>> = fuelRecordDao
        .observeForVehicle(vehicleId)
        .map { entities -> entities.map(FuelRecordEntity::toDomain) }

    override suspend fun getFuelRecord(id: Long): FuelRecord? = fuelRecordDao
        .findById(id)
        ?.toDomain()

    override suspend fun insertFuelRecord(record: FuelRecord): Long = fuelRecordDao
        .insert(record.toEntity())

    override suspend fun updateFuelRecord(record: FuelRecord) {
        fuelRecordDao.update(record.toEntity())
    }

    override suspend fun deleteFuelRecord(record: FuelRecord) {
        fuelRecordDao.delete(record.toEntity())
    }
}
