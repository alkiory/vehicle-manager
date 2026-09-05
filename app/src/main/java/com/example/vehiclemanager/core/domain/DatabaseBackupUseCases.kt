package com.example.vehiclemanager.core.domain

import javax.inject.Inject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val SUPPORTED_SCHEMA_VERSION = 1

class ExportDatabaseUseCase @Inject constructor(
    private val backupRepository: VehicleBackupRepository,
) {
    suspend operator fun invoke(): String {
        val snapshot = backupRepository.readSnapshot()
        return backupJson.encodeToString(VehicleBackupPayload.serializer(), snapshot.toPayload())
    }
}

class ImportDatabaseUseCase @Inject constructor(
    private val backupRepository: VehicleBackupRepository,
) {
    suspend operator fun invoke(serializedSnapshot: String) {
        val payload = runCatching {
            backupJson.decodeFromString(VehicleBackupPayload.serializer(), serializedSnapshot)
        }.getOrElse { error ->
            throw IllegalArgumentException("Invalid backup JSON", error)
        }
        val snapshot = payload.toSnapshot()
        validateSnapshot(snapshot)
        backupRepository.replaceSnapshot(snapshot)
    }

    private fun validateSnapshot(snapshot: VehicleBackupSnapshot) {
        val vehicleIds = snapshot.vehicles.map { it.id }
        require(vehicleIds.none { it <= 0 }) { "Vehicle IDs must be positive" }
        require(vehicleIds.toSet().size == vehicleIds.size) { "Vehicle IDs must be unique" }

        val knownVehicleIds = vehicleIds.toSet()
        require(snapshot.activeVehicleId == null || snapshot.activeVehicleId in knownVehicleIds) {
            "Active vehicle must reference an exported vehicle"
        }
        require(snapshot.fuelRecords.all { it.id > 0 && it.vehicleId in knownVehicleIds }) {
            "Fuel records must reference an exported vehicle"
        }
        require(snapshot.maintenanceRecords.all { it.id > 0 && it.vehicleId in knownVehicleIds }) {
            "Maintenance records must reference an exported vehicle"
        }
        require(snapshot.maintenanceSchedules.all { it.id > 0 && it.vehicleId in knownVehicleIds }) {
            "Maintenance schedules must reference an exported vehicle"
        }
        require(snapshot.fuelRecords.map { it.id }.toSet().size == snapshot.fuelRecords.size) {
            "Fuel record IDs must be unique"
        }
        require(snapshot.maintenanceRecords.map { it.id }.toSet().size == snapshot.maintenanceRecords.size) {
            "Maintenance record IDs must be unique"
        }
        require(snapshot.maintenanceSchedules.map { it.id }.toSet().size == snapshot.maintenanceSchedules.size) {
            "Maintenance schedule IDs must be unique"
        }
    }
}

@Serializable
private data class VehicleBackupPayload(
    val schemaVersion: Int,
    val activeVehicleId: Long?,
    val vehicles: List<VehiclePayload>,
    val fuelRecords: List<FuelRecordPayload>,
    val maintenanceRecords: List<MaintenanceRecordPayload>,
    val maintenanceSchedules: List<MaintenanceSchedulePayload>,
) {
    fun toSnapshot() = VehicleBackupSnapshot(
        activeVehicleId = activeVehicleId,
        vehicles = vehicles.map(VehiclePayload::toDomain),
        fuelRecords = fuelRecords.map(FuelRecordPayload::toDomain),
        maintenanceRecords = maintenanceRecords.map(MaintenanceRecordPayload::toDomain),
        maintenanceSchedules = maintenanceSchedules.map(MaintenanceSchedulePayload::toDomain),
    ).also {
        require(schemaVersion == SUPPORTED_SCHEMA_VERSION) {
            "Unsupported backup schema version: $schemaVersion"
        }
    }
}

@Serializable
private data class VehiclePayload(
    val id: Long,
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val vin: String?,
    val fuelType: String,
    val primaryOdometerKm: Long,
) {
    fun toDomain() = Vehicle(
        id = id,
        name = name,
        make = make,
        model = model,
        year = year,
        licensePlate = licensePlate,
        vin = vin,
        fuelType = runCatching { FuelType.valueOf(fuelType) }
            .getOrElse { throw IllegalArgumentException("Unknown fuel type: $fuelType") },
        primaryOdometerKm = primaryOdometerKm,
    )
}

@Serializable
private data class FuelRecordPayload(
    val id: Long,
    val vehicleId: Long,
    val timestampMs: Long,
    val odometerKm: Long,
    val litersX100: Int,
    val pricePerLiterCents: Long,
    val totalCostCents: Long,
    val isFullTank: Boolean,
    val stationName: String?,
    val notes: String?,
) {
    fun toDomain() = FuelRecord(
        id,
        vehicleId,
        timestampMs,
        odometerKm,
        litersX100,
        pricePerLiterCents,
        totalCostCents,
        isFullTank,
        stationName,
        notes,
    )
}

@Serializable
private data class MaintenanceRecordPayload(
    val id: Long,
    val vehicleId: Long,
    val title: String,
    val category: String,
    val costCents: Long,
    val odometerKm: Long,
    val timestampMs: Long,
    val notes: String?,
    val performedBy: String?,
) {
    fun toDomain() = MaintenanceRecord(
        id,
        vehicleId,
        title,
        runCatching { MaintenanceCategory.valueOf(category) }
            .getOrElse { throw IllegalArgumentException("Unknown maintenance category: $category") },
        costCents,
        odometerKm,
        timestampMs,
        notes,
        performedBy,
    )
}

@Serializable
private data class MaintenanceSchedulePayload(
    val id: Long,
    val vehicleId: Long,
    val serviceTitle: String,
    val intervalKm: Long?,
    val intervalMonths: Int?,
    val lastPerformedKm: Long?,
    val lastPerformedDateMs: Long?,
) {
    fun toDomain() = MaintenanceSchedule(
        id,
        vehicleId,
        serviceTitle,
        intervalKm,
        intervalMonths,
        lastPerformedKm,
        lastPerformedDateMs,
    )
}

private fun VehicleBackupSnapshot.toPayload() = VehicleBackupPayload(
    schemaVersion = SUPPORTED_SCHEMA_VERSION,
    activeVehicleId = activeVehicleId,
    vehicles = vehicles.map {
        VehiclePayload(
            it.id,
            it.name,
            it.make,
            it.model,
            it.year,
            it.licensePlate,
            it.vin,
            it.fuelType.name,
            it.primaryOdometerKm,
        )
    },
    fuelRecords = fuelRecords.map {
        FuelRecordPayload(
            it.id,
            it.vehicleId,
            it.timestampMs,
            it.odometerKm,
            it.litersX100,
            it.pricePerLiterCents,
            it.totalCostCents,
            it.isFullTank,
            it.stationName,
            it.notes,
        )
    },
    maintenanceRecords = maintenanceRecords.map {
        MaintenanceRecordPayload(
            it.id,
            it.vehicleId,
            it.title,
            it.category.name,
            it.costCents,
            it.odometerKm,
            it.timestampMs,
            it.notes,
            it.performedBy,
        )
    },
    maintenanceSchedules = maintenanceSchedules.map {
        MaintenanceSchedulePayload(
            it.id,
            it.vehicleId,
            it.serviceTitle,
            it.intervalKm,
            it.intervalMonths,
            it.lastPerformedKm,
            it.lastPerformedDateMs,
        )
    },
)

private val backupJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = false
}
