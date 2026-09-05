package com.example.vehiclemanager.core.data.maintenance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceRecordDao {
    @Query("SELECT * FROM maintenance_records WHERE vehicleId = :vehicleId ORDER BY timestampMs DESC, id DESC")
    fun observeForVehicle(vehicleId: Long): Flow<List<MaintenanceRecordEntity>>

    @Query("SELECT * FROM maintenance_records WHERE id = :id")
    suspend fun findById(id: Long): MaintenanceRecordEntity?

    @Query("SELECT * FROM maintenance_records ORDER BY id ASC")
    suspend fun findAll(): List<MaintenanceRecordEntity>

    @Query("DELETE FROM maintenance_records")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(record: MaintenanceRecordEntity): Long

    @Update
    suspend fun update(record: MaintenanceRecordEntity)

    @Delete
    suspend fun delete(record: MaintenanceRecordEntity)
}
