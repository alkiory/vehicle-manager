package com.example.vehiclemanager.core.data.settings

import com.example.vehiclemanager.core.domain.ReminderPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderDataModule {

    @Binds
    @Singleton
    abstract fun bindReminderPreferencesRepository(
        implementation: ReminderPreferencesRepositoryImpl,
    ): ReminderPreferencesRepository
}
