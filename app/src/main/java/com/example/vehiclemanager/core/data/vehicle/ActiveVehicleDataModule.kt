package com.example.vehiclemanager.core.data.vehicle

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
abstract class ActiveVehicleDataModule {

    @Binds
    @Singleton
    abstract fun bindActiveVehicleRepository(
        implementation: ActiveVehicleRepositoryImpl,
    ): ActiveVehicleRepository

    companion object {
        private const val ACTIVE_VEHICLE_DATASTORE_FILE = "active_vehicle_preferences"

        @Provides
        @Singleton
        fun provideActiveVehicleDataStore(
            @ApplicationContext context: Context,
        ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { context.preferencesDataStoreFile(ACTIVE_VEHICLE_DATASTORE_FILE) },
        )
    }
}
