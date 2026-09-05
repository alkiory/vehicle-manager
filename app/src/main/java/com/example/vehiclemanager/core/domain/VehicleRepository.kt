package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    val vehicles: Flow<List<Vehicle>>

    suspend fun getVehicle(id: Long): Vehicle?

    suspend fun insertVehicle(vehicle: Vehicle): Long

    suspend fun updateVehicle(vehicle: Vehicle)

    suspend fun deleteVehicle(vehicle: Vehicle)
}
