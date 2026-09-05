package com.example.vehiclemanager.core.domain

/** A complete, validated application snapshot independent of Room or Android. */
data class VehicleBackupSnapshot(
    val activeVehicleId: Long?,
    val vehicles: List<Vehicle>,
    val fuelRecords: List<FuelRecord>,
    val maintenanceRecords: List<MaintenanceRecord>,
    val maintenanceSchedules: List<MaintenanceSchedule>,
)

interface VehicleBackupRepository {
    suspend fun readSnapshot(): VehicleBackupSnapshot

    suspend fun replaceSnapshot(snapshot: VehicleBackupSnapshot)
}
