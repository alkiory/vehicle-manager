package com.example.vehiclemanager.core.data.database

import android.content.Context
import androidx.room.Room
import com.example.vehiclemanager.core.data.fuel.FuelRecordDao
import com.example.vehiclemanager.core.data.maintenance.MaintenanceRecordDao
import com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleDao
import com.example.vehiclemanager.core.data.vehicle.VehicleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideVehicleDatabase(
        @ApplicationContext context: Context,
    ): VehicleDatabase = Room.databaseBuilder(
        context,
        VehicleDatabase::class.java,
        DATABASE_NAME,
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()

    @Provides
    @Singleton
    fun provideVehicleDao(database: VehicleDatabase): VehicleDao = database.vehicleDao()

    @Provides
    @Singleton
    fun provideFuelRecordDao(database: VehicleDatabase): FuelRecordDao = database.fuelRecordDao()

    @Provides
    @Singleton
    fun provideMaintenanceRecordDao(database: VehicleDatabase): MaintenanceRecordDao =
        database.maintenanceRecordDao()

    @Provides
    @Singleton
    fun provideMaintenanceScheduleDao(database: VehicleDatabase): MaintenanceScheduleDao =
        database.maintenanceScheduleDao()

    private const val DATABASE_NAME = "vehicle_manager.db"
}
