package com.example.vehiclemanager.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class GetDashboardSummaryUseCaseTest {
    private val useCase = GetDashboardSummaryUseCase()

    @Test
    fun summaryAggregatesLatestFuelConsumptionAndUrgentServices() {
        val vehicle = vehicle(12_000)
        val oldFuel = fuel(timestampMs = 100, odometerKm = 11_000, litersX100 = 4_000, fullTank = true)
        val latestFuel = fuel(timestampMs = 200, odometerKm = 11_500, litersX100 = 4_000, fullTank = true)
        val overdue = schedule(intervalKm = 1_000, lastPerformedKm = 10_000)
        val healthy = schedule(id = 2, intervalKm = 5_000, lastPerformedKm = 10_000)

        val summary = useCase(
            activeVehicle = vehicle,
            fuelRecords = listOf(latestFuel, oldFuel),
            schedules = listOf(overdue, healthy),
            currentDateMs = 0,
        )

        assertEquals(vehicle, summary.activeVehicle)
        assertEquals(latestFuel, summary.latestFuelRecord)
        assertEquals(800L, summary.averageConsumptionLitersPer100KmX100)
        assertEquals(listOf(overdue.id), summary.maintenanceAlerts.map { it.schedule.id })
        assertEquals(2, summary.upcomingServices.size)
    }

    @Test
    fun summaryWithoutActiveVehicleDoesNotExposeVehicleDataOrAlerts() {
        val summary = useCase(
            activeVehicle = null,
            fuelRecords = emptyList(),
            schedules = listOf(schedule(intervalKm = 1_000, lastPerformedKm = 0)),
            currentDateMs = 0,
        )

        assertEquals(null, summary.activeVehicle)
        assertEquals(null, summary.latestFuelRecord)
        assertEquals(null, summary.averageConsumptionLitersPer100KmX100)
        assertEquals(emptyList<UpcomingService>(), summary.maintenanceAlerts)
        assertEquals(emptyList<UpcomingService>(), summary.upcomingServices)
    }

    private fun vehicle(odometerKm: Long) = Vehicle(
        id = 1,
        name = "Daily car",
        make = "Make",
        model = "Model",
        year = 2024,
        licensePlate = "TEST",
        vin = null,
        fuelType = FuelType.PETROL,
        primaryOdometerKm = odometerKm,
    )

    private fun fuel(
        timestampMs: Long,
        odometerKm: Long,
        litersX100: Int,
        fullTank: Boolean,
    ) = FuelRecord(
        id = timestampMs,
        vehicleId = 1,
        timestampMs = timestampMs,
        odometerKm = odometerKm,
        litersX100 = litersX100,
        pricePerLiterCents = 180,
        totalCostCents = 7_200,
        isFullTank = fullTank,
        stationName = null,
        notes = null,
    )

    private fun schedule(
        id: Long = 1,
        intervalKm: Long,
        lastPerformedKm: Long,
    ) = MaintenanceSchedule(
        id = id,
        vehicleId = 1,
        serviceTitle = "Oil change",
        intervalKm = intervalKm,
        intervalMonths = null,
        lastPerformedKm = lastPerformedKm,
        lastPerformedDateMs = null,
    )
}
