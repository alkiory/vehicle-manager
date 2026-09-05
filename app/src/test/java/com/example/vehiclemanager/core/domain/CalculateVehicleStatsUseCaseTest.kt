package com.example.vehiclemanager.core.domain

import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateVehicleStatsUseCaseTest {
    private val useCase = CalculateVehicleStatsUseCase()
    private val currentDate = epoch("2025-06-15")

    @Test
    fun allTimeAggregatesCostsDistanceMonthlySpendAndCharts() {
        val fuel = listOf(
            fuel("2025-01-10", 10_000, 180, 5_400),
            fuel("2025-03-10", 10_500, 200, 6_000),
        )
        val maintenance = listOf(maintenance("2025-03-12", 10_700, 10_000))

        val stats = useCase(fuel, maintenance, StatsPeriod.ALL_TIME, currentDate)

        assertEquals(11_400L, stats.totalFuelCostCents)
        assertEquals(10_000L, stats.totalMaintenanceCostCents)
        assertEquals(21_400L, stats.totalCostCents)
        assertEquals(3057L, stats.costPerKilometerCentsX100)
        assertEquals(10_700L, stats.averageMonthlySpendCents)
        assertEquals(700L, stats.totalDistanceKm)
        assertEquals(listOf(5_400L, 16_000L), stats.monthlyExpenditure.map { it.totalCostCents })
        assertEquals(listOf(180L, 200L), stats.monthlyFuelPrice.map { it.averagePricePerLiterCents })
    }

    @Test
    fun periodFiltersRecordsAtTheLowerAndUpperBounds() {
        val records = listOf(
            fuel("2025-05-15", 1_000, 180, 1_800),
            fuel("2025-06-15", 1_100, 180, 1_800),
        )

        val stats = useCase(records, emptyList(), StatsPeriod.LAST_30_DAYS, currentDate)

        assertEquals(1_800L, stats.totalFuelCostCents)
        assertEquals(1, stats.monthlyExpenditure.size)
    }

    @Test
    fun zeroDistanceProducesNullCostPerKilometer() {
        val stats = useCase(
            fuelRecords = listOf(fuel("2025-06-01", 1_000, 180, 1_800)),
            maintenanceRecords = emptyList(),
            period = StatsPeriod.YEAR_TO_DATE,
            currentDateMs = currentDate,
        )

        assertEquals(0L, stats.totalDistanceKm)
        assertEquals(null, stats.costPerKilometerCentsX100)
    }

    @Test
    fun weightedMonthlyFuelPriceUsesLitersNotRecordCount() {
        val stats = useCase(
            fuelRecords = listOf(
                fuel("2025-06-01", 1_000, 200, 2_000, litersX100 = 100),
                fuel("2025-06-02", 1_010, 100, 1_000, litersX100 = 300),
            ),
            maintenanceRecords = emptyList(),
            period = StatsPeriod.ALL_TIME,
            currentDateMs = currentDate,
        )

        assertEquals(125L, stats.monthlyFuelPrice.single().averagePricePerLiterCents)
    }

    private fun fuel(
        date: String,
        odometerKm: Long,
        priceCents: Long,
        totalCostCents: Long,
        litersX100: Int = 300,
    ) = FuelRecord(
        id = date.hashCode().toLong(),
        vehicleId = 1,
        timestampMs = epoch(date),
        odometerKm = odometerKm,
        litersX100 = litersX100,
        pricePerLiterCents = priceCents,
        totalCostCents = totalCostCents,
        isFullTank = true,
        stationName = null,
        notes = null,
    )

    private fun maintenance(date: String, odometerKm: Long, costCents: Long) = MaintenanceRecord(
        id = date.hashCode().toLong(),
        vehicleId = 1,
        title = "Service",
        category = MaintenanceCategory.OTHER,
        costCents = costCents,
        odometerKm = odometerKm,
        timestampMs = epoch(date),
        notes = null,
        performedBy = null,
    )

    private fun epoch(date: String): Long = LocalDate.parse(date)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}
