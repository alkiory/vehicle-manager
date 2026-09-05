package com.example.vehiclemanager.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vehiclemanager.core.data.vehicle.VehicleEntity
import com.example.vehiclemanager.core.data.vehicle.VehicleDao

/**
 * Local, offline-first database for Vehicle Manager.
 *
 * Feature entities are added as their data layers are implemented. Keeping the
 * database as the single Room entry point lets those features share one schema.
 */
@Database(
    entities = [VehicleEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class VehicleDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
}
