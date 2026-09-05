package com.example.vehiclemanager.core.data.vehicle

import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.Vehicle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VehicleRepositoryTest {
    @Test
    fun entityMappingPreservesAllVehicleFields() {
        val entity = VehicleEntity(
            id = 7,
            name = "Daily driver",
            make = "Toyota",
            model = "Prius",
            year = 2022,
            licensePlate = "ABC-123",
            vin = "JTDBR32E720000007",
            fuelType = FuelType.HYBRID,
            primaryOdometerKm = 42_500,
        )

        assertEquals(
            Vehicle(
                id = 7,
                name = "Daily driver",
                make = "Toyota",
                model = "Prius",
                year = 2022,
                licensePlate = "ABC-123",
                vin = "JTDBR32E720000007",
                fuelType = FuelType.HYBRID,
                primaryOdometerKm = 42_500,
            ),
            entity.toDomain(),
        )
    }

    @Test
    fun repositoryMapsObservedEntitiesToDomainModels() = runBlocking {
        val dao = FakeVehicleDao(
            entities = listOf(
                VehicleEntity(
                    id = 1,
                    name = "Work van",
                    make = "Ford",
                    model = "Transit",
                    year = 2020,
                    licensePlate = "VAN-001",
                    vin = null,
                    fuelType = FuelType.DIESEL,
                    primaryOdometerKm = 100_000,
                ),
            ),
        )
        val repository = VehicleRepositoryImpl(dao)

        assertEquals("Work van", repository.vehicles.first().single().name)
        assertNull(repository.getVehicle(999))
    }

    private class FakeVehicleDao(
        private val entities: List<VehicleEntity>,
    ) : VehicleDao {
        override fun observeAll() = flowOf(entities)

        override suspend fun findById(id: Long): VehicleEntity? = entities.firstOrNull { it.id == id }

        override suspend fun findAll(): List<VehicleEntity> = entities

        override suspend fun deleteAll() = Unit

        override suspend fun insert(vehicle: VehicleEntity): Long = vehicle.id

        override suspend fun update(vehicle: VehicleEntity) = Unit

        override suspend fun delete(vehicle: VehicleEntity) = Unit
    }
}
