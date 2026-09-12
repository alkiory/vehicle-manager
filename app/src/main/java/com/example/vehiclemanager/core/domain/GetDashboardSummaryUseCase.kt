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

        val costPerKmEurosX1000 = calculateCostPerKm(fuelRecords)

        return DashboardSummary(
            activeVehicle = activeVehicle,
            latestFuelRecord = latestFuelRecord,
            averageConsumptionLitersPer100KmX100 =
                (consumption as? FuelConsumptionResult.Calculated)?.litersPer100KmX100,
            costPerKmEurosX1000 = costPerKmEurosX1000,
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
        val distanceKm = maxOdometer - minOdometer
        if (distanceKm <= 0) return null
        val totalCostCents = fuelRecords.sumOf { it.totalCostCents }
        if (totalCostCents <= 0) return null
        // Calculate cost per km in euros: (totalCostCents / 100) / distanceKm
        // Return value in thousandths of euros ( euros * 1000 ) for precision
        return ((totalCostCents / 100L) * 1000L) / distanceKm
    }
}

data class DashboardSummary(
    val activeVehicle: Vehicle? = null,
    val latestFuelRecord: FuelRecord? = null,
    val averageConsumptionLitersPer100KmX100: Long? = null,
    val costPerKmEurosX1000: Long? = null,
    val maintenanceAlerts: List<UpcomingService> = emptyList(),
    val upcomingServices: List<UpcomingService> = emptyList(),
)
