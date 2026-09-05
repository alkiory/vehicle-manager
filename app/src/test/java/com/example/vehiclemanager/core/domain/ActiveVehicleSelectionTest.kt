package com.example.vehiclemanager.core.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ActiveVehicleSelectionTest {
    private val firstVehicle = vehicle(id = 1)
    private val secondVehicle = vehicle(id = 2)

    @Test
    fun emptyVehicleListHasNoActiveVehicle() {
        assertNull(selectActiveVehicle(emptyList(), storedVehicleId = null))
    }

    @Test
    fun firstVehicleIsSelectedWhenNoStoredSelectionExists() {
        assertEquals(firstVehicle, selectActiveVehicle(listOf(firstVehicle, secondVehicle), null))
    }

    @Test
    fun storedVehicleIsSelectedWhenItStillExists() {
        assertEquals(secondVehicle, selectActiveVehicle(listOf(firstVehicle, secondVehicle), 2))
    }

    @Test
    fun missingStoredVehicleFallsBackToFirstVehicle() {
        assertEquals(firstVehicle, selectActiveVehicle(listOf(firstVehicle, secondVehicle), 99))
    }

    private fun vehicle(id: Long) = Vehicle(
        id = id,
        name = "Vehicle $id",
        make = "Make",
        model = "Model",
        year = 2024,
        licensePlate = "PLATE-$id",
        vin = null,
        fuelType = FuelType.PETROL,
        primaryOdometerKm = 1_000,
    )
}
