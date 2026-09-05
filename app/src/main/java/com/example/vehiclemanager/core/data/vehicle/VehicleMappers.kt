package com.example.vehiclemanager.core.data.vehicle

import com.example.vehiclemanager.core.domain.Vehicle

fun VehicleEntity.toDomain(): Vehicle = Vehicle(
    id = id,
    name = name,
    make = make,
    model = model,
    year = year,
    licensePlate = licensePlate,
    vin = vin,
    fuelType = fuelType,
    primaryOdometerKm = primaryOdometerKm,
)

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
    id = id,
    name = name,
    make = make,
    model = model,
    year = year,
    licensePlate = licensePlate,
    vin = vin,
    fuelType = fuelType,
    primaryOdometerKm = primaryOdometerKm,
)
