package com.example.vehiclemanager.core.domain

import javax.inject.Inject
import kotlin.math.abs

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

        val costPerKmCentsX100 = calculateCostPerKm(fuelRecords)

        return DashboardSummary(
            activeVehicle = activeVehicle,
            latestFuelRecord = latestFuelRecord,
            averageConsumptionLitersPer100KmX100 =
                (consumption as? FuelConsumptionResult.Calculated)?.litersPer100KmX100,
            costPerKmCentsX100 = costPerKmCentsX100,
            maintenanceAlerts = upcomingServices.filter { it.status != ServiceStatus.OK },
            upcomingServices = upcomingServices,
        )
    }

    private fun calculateCostPerKm(fuelRecords: List<FuelRecord>): Long? {
        if (fuelRecords.size < 2) return null
        val sorted = fuelRecords.sortedWith(
            compareBy<FuelRecord> { it.timestampMs }.thenBy { it.id },
        )
        val minOdometer = sorted.first().odometerKm
        val maxOdometer = sorted.last().odometerKm
        val distanceKm = abs(maxOdometer - minOdometer)
        if (distanceKm <= 0) return null
        val totalCostCents = fuelRecords.sumOf { it.totalCostCents }
        if (totalCostCents <= 0) return null
        return (totalCostCents * 100L) / distanceKm
    }
}

data class DashboardSummary(
    val activeVehicle: Vehicle? = null,
    val latestFuelRecord: FuelRecord? = null,
    val averageConsumptionLitersPer100KmX100: Long? = null,
    val costPerKmCentsX100: Long? = null,
    val maintenanceAlerts: List<UpcomingService> = emptyList(),
    val upcomingServices: List<UpcomingService> = emptyList(),
)
