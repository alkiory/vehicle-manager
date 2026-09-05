package com.example.vehiclemanager.core.data.fuel

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.vehiclemanager.core.data.vehicle.VehicleEntity

@Entity(
    tableName = "fuel_records",
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
data class FuelRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val timestampMs: Long,
    val odometerKm: Long,
    val litersX100: Int,
    val pricePerLiterCents: Long,
    val totalCostCents: Long,
    val isFullTank: Boolean,
    val stationName: String?,
    val notes: String?,
)
