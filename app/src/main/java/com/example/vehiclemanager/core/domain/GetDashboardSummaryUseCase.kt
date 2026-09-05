package com.example.vehiclemanager.core.domain

import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor() {
    private val getUpcomingServices = GetUpcomingServicesUseCase()
    private val calculateFuelConsumption = CalculateFuelConsumptionUseCase()

    operator fun invoke(
        activeVehicle: Vehicle?,
        fuelRecords: List<FuelRecord>,
        schedules: List<MaintenanceSchedule>,
        currentDateMs: Long,
    ): DashboardSummary {
        val latestFuelRecord = fuelRecords.maxWithOrNull(
            compareBy<FuelRecord> { it.timestampMs }.thenBy { it.id },
        )
        val consumption = calculateFuelConsumption(fuelRecords)
        val upcomingServices = if (activeVehicle == null) {
            emptyList()
        } else {
            getUpcomingServices(
                schedules = schedules,
                currentOdometerKm = activeVehicle.primaryOdometerKm,
                currentDateMs = currentDateMs,
            )
        }

        return DashboardSummary(
            activeVehicle = activeVehicle,
            latestFuelRecord = latestFuelRecord,
            averageConsumptionLitersPer100KmX100 =
                (consumption as? FuelConsumptionResult.Calculated)?.litersPer100KmX100,
            maintenanceAlerts = upcomingServices.filter { it.status != ServiceStatus.OK },
            upcomingServices = upcomingServices,
        )
    }
}

data class DashboardSummary(
    val activeVehicle: Vehicle? = null,
    val latestFuelRecord: FuelRecord? = null,
    val averageConsumptionLitersPer100KmX100: Long? = null,
    val maintenanceAlerts: List<UpcomingService> = emptyList(),
    val upcomingServices: List<UpcomingService> = emptyList(),
)
