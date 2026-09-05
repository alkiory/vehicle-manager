package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MaintenanceScheduleDataModule {
    @Binds
    @Singleton
    abstract fun bindMaintenanceScheduleRepository(
        implementation: MaintenanceScheduleRepositoryImpl,
    ): MaintenanceScheduleRepository
}
