package com.example.vehiclemanager.core.data.maintenance

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vehiclemanager.core.data.database.VehicleDatabase
import com.example.vehiclemanager.core.data.vehicle.VehicleEntity
import com.example.vehiclemanager.core.domain.FuelType
import com.example.vehiclemanager.core.domain.MaintenanceCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaintenanceRecordDaoTest {
    private lateinit var database: VehicleDatabase
    private lateinit var vehicleDao: com.example.vehiclemanager.core.data.vehicle.VehicleDao
    private lateinit var maintenanceDao: MaintenanceRecordDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VehicleDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        vehicleDao = database.vehicleDao()
        maintenanceDao = database.maintenanceRecordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun recordsAreFilteredAndSortedByTimestamp() = runBlocking {
        val vehicleId = vehicleDao.insert(vehicle())
        val otherVehicleId = vehicleDao.insert(vehicle())
        maintenanceDao.insert(record(vehicleId = vehicleId, timestampMs = 100))
        maintenanceDao.insert(record(vehicleId = vehicleId, timestampMs = 300))
        maintenanceDao.insert(record(vehicleId = otherVehicleId, timestampMs = 500))

        assertEquals(
            listOf(300L, 100L),
            maintenanceDao.observeForVehicle(vehicleId).first().map { it.timestampMs },
        )
    }

    @Test
    fun deletingVehicleCascadesToMaintenanceRecords() = runBlocking {
        val vehicleId = vehicleDao.insert(vehicle())
        val recordId = maintenanceDao.insert(record(vehicleId = vehicleId, timestampMs = 100))

        vehicleDao.delete(vehicle().copy(id = vehicleId))

        assertNull(maintenanceDao.findById(recordId))
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

    private fun record(vehicleId: Long, timestampMs: Long) = MaintenanceRecordEntity(
        vehicleId = vehicleId,
        title = "Inspection",
        category = MaintenanceCategory.GENERAL_INSPECTION,
        costCents = 5_000,
        odometerKm = 1_000,
        timestampMs = timestampMs,
        notes = null,
        performedBy = null,
    )
}
