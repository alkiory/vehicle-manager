package com.example.vehiclemanager.core.data.maintenance

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vehiclemanager.core.data.database.VehicleDatabase
import com.example.vehiclemanager.core.data.vehicle.VehicleEntity
import com.example.vehiclemanager.core.domain.FuelType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaintenanceScheduleDaoTest {
    private lateinit var database: VehicleDatabase
    private lateinit var vehicleDao: com.example.vehiclemanager.core.data.vehicle.VehicleDao
    private lateinit var scheduleDao: MaintenanceScheduleDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VehicleDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        vehicleDao = database.vehicleDao()
        scheduleDao = database.maintenanceScheduleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun schedulesAreFilteredByVehicleAndOrderedByTitle() = runBlocking {
        val vehicleId = vehicleDao.insert(vehicle())
        val otherVehicleId = vehicleDao.insert(vehicle())
        scheduleDao.insert(schedule(vehicleId, "Tires"))
        scheduleDao.insert(schedule(vehicleId, "Oil change"))
        scheduleDao.insert(schedule(otherVehicleId, "Brakes"))

        val schedules = scheduleDao.observeForVehicle(vehicleId).first()

        assertEquals(listOf("Oil change", "Tires"), schedules.map { it.serviceTitle })
    }

    @Test
    fun deletingVehicleCascadesToSchedules() = runBlocking {
        val vehicleId = vehicleDao.insert(vehicle())
        val scheduleId = scheduleDao.insert(schedule(vehicleId, "Oil change"))

        vehicleDao.delete(vehicle().copy(id = vehicleId))

        assertEquals(null, scheduleDao.findById(scheduleId))
    }

    private fun vehicle() = VehicleEntity(
        name = "Test vehicle",
        make = "Make",
        model = "Model",
        year = 2024,
        licensePlate = "TEST",
        vin = null,
        fuelType = FuelType.PETROL,
        primaryOdometerKm = 1_000,
    )

    private fun schedule(vehicleId: Long, title: String) = MaintenanceScheduleEntity(
        vehicleId = vehicleId,
        serviceTitle = title,
        intervalKm = 10_000,
        intervalMonths = null,
        lastPerformedKm = 1_000,
        lastPerformedDateMs = null,
    )
}
