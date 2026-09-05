package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.Flow

interface MaintenanceRecordRepository {
    fun observeMaintenanceRecords(vehicleId: Long): Flow<List<MaintenanceRecord>>

    suspend fun getMaintenanceRecord(id: Long): MaintenanceRecord?

    suspend fun insertMaintenanceRecord(record: MaintenanceRecord): Long

    suspend fun updateMaintenanceRecord(record: MaintenanceRecord)

    suspend fun deleteMaintenanceRecord(record: MaintenanceRecord)
}
