package com.example.vehiclemanager.core.data.vehicle

import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ActiveVehicleDataModule {
    @Binds
    @Singleton
    abstract fun bindActiveVehicleRepository(
        implementation: ActiveVehicleRepositoryImpl,
    ): ActiveVehicleRepository
}
