package com.example.vehiclemanager.core.data.database

import com.example.vehiclemanager.core.domain.VehicleBackupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupDataModule {
    @Binds
    @Singleton
    abstract fun bindVehicleBackupRepository(
        implementation: RoomVehicleBackupRepository,
    ): VehicleBackupRepository
}
