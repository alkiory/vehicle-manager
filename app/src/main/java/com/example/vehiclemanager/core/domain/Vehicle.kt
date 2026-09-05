package com.example.vehiclemanager.core.domain

data class Vehicle(
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
