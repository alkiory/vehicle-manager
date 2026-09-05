package com.example.vehiclemanager.core.data.fuel

import com.example.vehiclemanager.core.domain.FuelRecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FuelDataModule {
    @Binds
    @Singleton
    abstract fun bindFuelRecordRepository(
        implementation: FuelRecordRepositoryImpl,
    ): FuelRecordRepository
}
