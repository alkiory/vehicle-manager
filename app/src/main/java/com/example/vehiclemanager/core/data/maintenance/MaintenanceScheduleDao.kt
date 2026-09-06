package com.example.vehiclemanager.core.data.maintenance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceScheduleDao {
    @Query("SELECT * FROM maintenance_schedules WHERE vehicleId = :vehicleId ORDER BY serviceTitle ASC, id ASC")
    fun observeForVehicle(vehicleId: Long): Flow<List<MaintenanceScheduleEntity>>

    @Query("SELECT * FROM maintenance_schedules WHERE id = :id")
    suspend fun findById(id: Long): MaintenanceScheduleEntity?

    @Query("SELECT * FROM maintenance_schedules ORDER BY id ASC")
    suspend fun findAll(): List<MaintenanceScheduleEntity>

    @Query("SELECT * FROM maintenance_schedules ORDER BY id ASC")
    fun observeAll(): Flow<List<MaintenanceScheduleEntity>>

    @Query("DELETE FROM maintenance_schedules")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(schedule: MaintenanceScheduleEntity): Long

    @Update
    suspend fun update(schedule: MaintenanceScheduleEntity)

    @Delete
    suspend fun delete(schedule: MaintenanceScheduleEntity)
}
