package com.example.vehiclemanager.core.data.database

import android.content.Context
import androidx.room.Room
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
    ).build()

    @Provides
    @Singleton
    fun provideVehicleDao(database: VehicleDatabase): VehicleDao = database.vehicleDao()

    private const val DATABASE_NAME = "vehicle_manager.db"
}
