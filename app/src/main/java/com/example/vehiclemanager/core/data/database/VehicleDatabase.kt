package com.example.vehiclemanager.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vehiclemanager.core.data.fuel.FuelRecordDao
import com.example.vehiclemanager.core.data.fuel.FuelRecordEntity
import com.example.vehiclemanager.core.data.vehicle.VehicleDao
import com.example.vehiclemanager.core.data.vehicle.VehicleEntity

/**
 * Local, offline-first database for Vehicle Manager.
 *
 * Feature entities are added as their data layers are implemented. Keeping the
 * database as the single Room entry point lets those features share one schema.
 */
@Database(
    entities = [VehicleEntity::class, FuelRecordEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class VehicleDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun fuelRecordDao(): FuelRecordDao
}
