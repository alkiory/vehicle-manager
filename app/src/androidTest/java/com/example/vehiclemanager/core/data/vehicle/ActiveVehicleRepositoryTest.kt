package com.example.vehiclemanager.core.data.vehicle

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ActiveVehicleRepositoryTest {
    private lateinit var repository: ActiveVehicleRepositoryImpl
    private lateinit var vehicles: MutableStateFlow<List<Vehicle>>

    // Un único DataStore por proceso y archivo (mismo comportamiento que el delegate
    // `preferencesDataStore`); crear más de uno para el mismo archivo lanza IllegalStateException.
    private companion object {
        private val dataStore: DataStore<Preferences> by lazy {
            PreferenceDataStoreFactory.create(
                scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
                produceFile = {
                    ApplicationProvider.getApplicationContext<Context>()
                        .preferencesDataStoreFile("active_vehicle_preferences")
                },
            )
        }
    }

    @Before
    fun setUp() {
        vehicles = MutableStateFlow(emptyList())
        repository = ActiveVehicleRepositoryImpl(
            dataStore = dataStore,
            vehicleRepository = FakeVehicleRepository(vehicleFlow = vehicles),
        )
    }

    @After
    fun tearDown() = runBlocking {
        repository.clearActiveVehicle()
    }

    @Test
    fun firstVehicleBecomesActiveAutomatically() = runBlocking {
        val first = vehicle(1)
        val second = vehicle(2)
        vehicles.value = listOf(first, second)

        assertEquals(first, repository.activeVehicle.first { it != null })
    }

    @Test
    fun selectedVehicleIsRestoredAcrossRepositoryInstances() = runBlocking {
        val first = vehicle(1)
        val second = vehicle(2)
        vehicles.value = listOf(first, second)

        repository.setActiveVehicle(second.id)
        val recreatedRepository = ActiveVehicleRepositoryImpl(
            dataStore = dataStore,
            vehicleRepository = FakeVehicleRepository(vehicleFlow = vehicles),
        )

        assertEquals(second, recreatedRepository.activeVehicle.first { it?.id == second.id })
        recreatedRepository.clearActiveVehicle()
    }

    @Test
    fun selectedVehicleIsRestoredAndClearFallsBackToFirst() = runBlocking {
        val first = vehicle(1)
        val second = vehicle(2)
        vehicles.value = listOf(first, second)

        repository.setActiveVehicle(second.id)
        assertEquals(second, repository.activeVehicle.first { it?.id == second.id })

        repository.clearActiveVehicle()
        assertEquals(first, repository.activeVehicle.first { it?.id == first.id })
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

    private class FakeVehicleRepository(
        private val vehicleFlow: Flow<List<Vehicle>>,
    ) : VehicleRepository {
        override val vehicles: Flow<List<Vehicle>> = vehicleFlow

        override suspend fun getVehicle(id: Long): Vehicle? = vehicleFlow.first()
            .firstOrNull { it.id == id }

        override suspend fun insertVehicle(vehicle: Vehicle): Long = vehicle.id

        override suspend fun updateVehicle(vehicle: Vehicle) = Unit

        override suspend fun deleteVehicle(vehicle: Vehicle) = Unit
    }
}
