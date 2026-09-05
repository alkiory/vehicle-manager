package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.StateFlow

interface ActiveVehicleRepository {
    val activeVehicle: StateFlow<Vehicle?>

    suspend fun setActiveVehicle(vehicleId: Long)

    suspend fun clearActiveVehicle()
}
