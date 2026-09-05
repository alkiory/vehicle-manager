package com.example.vehiclemanager.core.data.vehicle

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vehiclemanager.core.domain.FuelType

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val vin: String?,
    val fuelType: FuelType,
    val primaryOdometerKm: Long,
)
