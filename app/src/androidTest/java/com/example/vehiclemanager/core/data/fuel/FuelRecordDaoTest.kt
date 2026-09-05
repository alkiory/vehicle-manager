package com.example.vehiclemanager.core.data.fuel

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
class FuelRecordDaoTest {
    private lateinit var database: VehicleDatabase
    private lateinit var vehicleDao: com.example.vehiclemanager.core.data.vehicle.VehicleDao
    private lateinit var fuelDao: FuelRecordDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VehicleDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        vehicleDao = database.vehicleDao()
        fuelDao = database.fuelRecordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun recordsAreFilteredAndSortedByVehicleAndTimestamp() = runBlocking {
        val vehicleId = vehicleDao.insert(vehicle())
        val otherVehicleId = vehicleDao.insert(vehicle())
        fuelDao.insert(record(vehicleId = vehicleId, timestampMs = 100))
        fuelDao.insert(record(vehicleId = vehicleId, timestampMs = 300))
        fuelDao.insert(record(vehicleId = otherVehicleId, timestampMs = 500))

        val records = fuelDao.observeForVehicle(vehicleId).first()

        assertEquals(listOf(300L, 100L), records.map { it.timestampMs })
    }

    @Test
    fun deletingVehicleCascadesToFuelRecords() = runBlocking {
        val vehicleId = vehicleDao.insert(vehicle())
        val fuelId = fuelDao.insert(record(vehicleId = vehicleId, timestampMs = 100))

        vehicleDao.delete(vehicle().copy(id = vehicleId))

        assertEquals(null, fuelDao.findById(fuelId))
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

    private fun record(vehicleId: Long, timestampMs: Long) = FuelRecordEntity(
        vehicleId = vehicleId,
        timestampMs = timestampMs,
        odometerKm = 1_000,
        litersX100 = 3_000,
        pricePerLiterCents = 180,
        totalCostCents = 5_400,
        isFullTank = true,
        stationName = null,
        notes = null,
    )
}
