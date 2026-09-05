package com.example.vehiclemanager.core.data.vehicle

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.vehiclemanager.core.domain.ActiveVehicleRepository
import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleRepository
import com.example.vehiclemanager.core.domain.selectActiveVehicle
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Singleton
class ActiveVehicleRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val vehicleRepository: VehicleRepository,
) : ActiveVehicleRepository {
    override val activeVehicle: StateFlow<Vehicle?> = combine(
        vehicleRepository.vehicles,
        dataStore.data,
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
        dataStore.edit { preferences ->
            preferences[ACTIVE_VEHICLE_ID] = vehicleId
        }
    }

    override suspend fun clearActiveVehicle() {
        dataStore.edit { preferences ->
            preferences.remove(ACTIVE_VEHICLE_ID)
        }
    }

    private companion object {
        val ACTIVE_VEHICLE_ID = longPreferencesKey("active_vehicle_id")
    }
}
