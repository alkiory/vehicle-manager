package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MaintenanceDataModule {
    @Binds
    @Singleton
    abstract fun bindMaintenanceRecordRepository(
        implementation: MaintenanceRecordRepositoryImpl,
    ): MaintenanceRecordRepository
}
