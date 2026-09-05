package com.example.vehiclemanager.core.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

@Serializable
data object VehiclesRoute

@Serializable
data class AddEditVehicleRoute(
    val vehicleId: Long? = null,
)

@Serializable
data object FuelRoute

@Serializable
data object AddFuelRoute

@Serializable
data class FuelDetailRoute(
    val fuelRecordId: Long,
)

@Serializable
data object MaintenanceRoute

@Serializable
data class AddEditMaintenanceRoute(
    val recordId: Long? = null,
)

@Serializable
data object StatsRoute

@Serializable
data object SettingsRoute
