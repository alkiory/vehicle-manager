package com.example.vehiclemanager.core.data.maintenance

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.vehiclemanager.core.data.vehicle.VehicleEntity

@Entity(
    tableName = "maintenance_schedules",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["vehicleId"])],
)
data class MaintenanceScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val serviceTitle: String,
    val intervalKm: Long?,
    val intervalMonths: Int?,
    val lastPerformedKm: Long?,
    val lastPerformedDateMs: Long?,
)
