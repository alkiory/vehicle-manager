package com.example.vehiclemanager.core.data.maintenance

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.vehiclemanager.core.data.vehicle.VehicleEntity
import com.example.vehiclemanager.core.domain.MaintenanceCategory

@Entity(
    tableName = "maintenance_records",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["vehicleId", "timestampMs"])],
)
data class MaintenanceRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val title: String,
    val category: MaintenanceCategory,
    val costCents: Long,
    val odometerKm: Long,
    val timestampMs: Long,
    val notes: String?,
    val performedBy: String?,
)
