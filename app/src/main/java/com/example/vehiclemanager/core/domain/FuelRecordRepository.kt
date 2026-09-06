package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.Flow

interface FuelRecordRepository {
    fun observeFuelRecords(vehicleId: Long): Flow<List<FuelRecord>>

    /**
     * Observe recent fuel prices across all vehicles.
     * Used for price alert comparisons.
     */
    fun observeRecentPrices(): Flow<List<FuelRecord>>

    suspend fun getFuelRecord(id: Long): FuelRecord?

    suspend fun insertFuelRecord(record: FuelRecord): Long

    suspend fun updateFuelRecord(record: FuelRecord)

    suspend fun deleteFuelRecord(record: FuelRecord)
}
