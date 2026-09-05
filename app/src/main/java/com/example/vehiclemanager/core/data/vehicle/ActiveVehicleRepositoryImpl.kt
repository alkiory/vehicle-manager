package com.example.vehiclemanager.core.data.vehicle

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.VehicleRepository
import com.example.vehiclemanager.core.domain.selectActiveVehicle
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private val Context.activeVehicleDataStore by preferencesDataStore(
    name = "active_vehicle_preferences",
)

@Singleton
class ActiveVehicleRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val vehicleRepository: VehicleRepository,
) : ActiveVehicleRepository {
    override val activeVehicle: StateFlow<com.example.vehiclemanager.core.domain.Vehicle?> = combine(
        vehicleRepository.vehicles,
        context.activeVehicleDataStore.data,
    ) { vehicles, preferences ->
        selectActiveVehicle(vehicles, preferences[ACTIVE_VEHICLE_ID])
    }
        .stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    override suspend fun setActiveVehicle(vehicleId: Long) {
        require(vehicleRepository.getVehicle(vehicleId) != null) {
            "Cannot select a vehicle that does not exist: $vehicleId"
        }
        context.activeVehicleDataStore.edit { preferences ->
            preferences[ACTIVE_VEHICLE_ID] = vehicleId
        }
    }

    override suspend fun clearActiveVehicle() {
        context.activeVehicleDataStore.edit { preferences ->
            preferences.remove(ACTIVE_VEHICLE_ID)
        }
    }

    private companion object {
        val ACTIVE_VEHICLE_ID = longPreferencesKey("active_vehicle_id")
    }
}
