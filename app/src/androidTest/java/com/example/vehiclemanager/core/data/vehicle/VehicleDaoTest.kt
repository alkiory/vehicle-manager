package com.example.vehiclemanager.core.data.vehicle

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vehiclemanager.core.data.database.VehicleDatabase
import com.example.vehiclemanager.core.domain.FuelType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleDaoTest {
    private lateinit var database: VehicleDatabase
    private lateinit var dao: VehicleDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VehicleDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.vehicleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun vehicleCrudPersistsAndReadsAllFields() = runBlocking {
        val vehicle = VehicleEntity(
            name = "Family car",
            make = "Honda",
            model = "Civic",
            year = 2021,
            licensePlate = "CAR-021",
            vin = "2HGFC2F69MH000021",
            fuelType = FuelType.PETROL,
            primaryOdometerKm = 18_250,
        )

        val id = dao.insert(vehicle)
        val inserted = dao.findById(id)

        assertNotNull(inserted)
        assertEquals(vehicle.copy(id = id), inserted)
        assertEquals(1, dao.observeAll().first().size)

        val updated = inserted!!.copy(name = "Updated car", primaryOdometerKm = 19_000)
        dao.update(updated)
        assertEquals(updated, dao.findById(id))

        dao.delete(updated)
        assertEquals(emptyList<VehicleEntity>(), dao.observeAll().first())
    }
}
