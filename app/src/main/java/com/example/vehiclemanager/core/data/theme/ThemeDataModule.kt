package com.example.vehiclemanager.core.data.theme

import com.example.vehiclemanager.core.domain.ThemePreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ThemeDataModule {
    @Binds
    @Singleton
    abstract fun bindThemePreferenceRepository(
        implementation: ThemePreferenceRepositoryImpl,
    ): ThemePreferenceRepository
}
