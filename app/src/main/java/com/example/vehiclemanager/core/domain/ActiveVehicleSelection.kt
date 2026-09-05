package com.example.vehiclemanager.core.domain

fun selectActiveVehicle(
    vehicles: List<Vehicle>,
    storedVehicleId: Long?,
): Vehicle? = storedVehicleId
    ?.let { id -> vehicles.firstOrNull { it.id == id } }
    ?: vehicles.firstOrNull()
