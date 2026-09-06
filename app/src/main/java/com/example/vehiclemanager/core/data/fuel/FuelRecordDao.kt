package com.example.vehiclemanager.core.data.fuel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelRecordDao {
    @Query("SELECT * FROM fuel_records WHERE vehicleId = :vehicleId ORDER BY timestampMs DESC, id DESC")
    fun observeForVehicle(vehicleId: Long): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records ORDER BY timestampMs DESC, id DESC")
    fun observeRecent(): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records WHERE id = :id")
    suspend fun findById(id: Long): FuelRecordEntity?

    @Query("SELECT * FROM fuel_records ORDER BY id ASC")
    suspend fun findAll(): List<FuelRecordEntity>

    @Query("DELETE FROM fuel_records")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(record: FuelRecordEntity): Long

    @Update
    suspend fun update(record: FuelRecordEntity)

    @Delete
    suspend fun delete(record: FuelRecordEntity)
}
