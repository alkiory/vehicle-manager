package com.example.vehiclemanager.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateFuelConsumptionUseCaseTest {
    private val calculate = CalculateFuelConsumptionUseCase()

    @Test
    fun includesPartialRefuelBetweenFullTankCheckpoints() {
        val result = calculate(
            listOf(
                record(id = 1, odometerKm = 10_000, litersX100 = 4_500, fullTank = true),
                record(id = 2, odometerKm = 10_200, litersX100 = 1_000, fullTank = false),
                record(id = 3, odometerKm = 10_500, litersX100 = 3_500, fullTank = true),
            ),
        )

        assertEquals(
            FuelConsumptionResult.Calculated(
                litersPer100KmX100 = 900,
                intervalsUsed = 1,
            ),
            result,
        )
    }

    @Test
    fun sortsRecordsBeforeCalculating() {
        val result = calculate(
            listOf(
                record(id = 3, timestampMs = 300, odometerKm = 10_500, litersX100 = 3_500, fullTank = true),
                record(id = 1, timestampMs = 100, odometerKm = 10_000, litersX100 = 4_500, fullTank = true),
                record(id = 2, timestampMs = 200, odometerKm = 10_200, litersX100 = 1_000, fullTank = false),
            ),
        )

        assertEquals(900L, (result as FuelConsumptionResult.Calculated).litersPer100KmX100)
    }

    @Test
    fun aggregatesMultipleValidIntervalsByDistance() {
        val result = calculate(
            listOf(
                record(id = 1, odometerKm = 1_000, litersX100 = 4_000, fullTank = true),
                record(id = 2, odometerKm = 1_500, litersX100 = 3_000, fullTank = true),
                record(id = 3, odometerKm = 2_000, litersX100 = 4_000, fullTank = true),
            ),
        )

        // 70 L / 1,000 km = 7 L/100 km = 700 in x100 units.
        assertEquals(700L, (result as FuelConsumptionResult.Calculated).litersPer100KmX100)
        assertEquals(2, result.intervalsUsed)
    }

    @Test
    fun returnsInsufficientDataWithoutTwoFullTanks() {
        val result = calculate(
            listOf(record(id = 1, odometerKm = 1_000, litersX100 = 4_000, fullTank = true)),
        )

        assertEquals(FuelConsumptionResult.InsufficientData, result)
    }

    @Test
    fun ignoresZeroDistanceIntervals() {
        val result = calculate(
            listOf(
                record(id = 1, odometerKm = 1_000, litersX100 = 4_000, fullTank = true),
                record(id = 2, odometerKm = 1_000, litersX100 = 4_000, fullTank = true),
            ),
        )

        assertEquals(FuelConsumptionResult.InsufficientData, result)
    }

    @Test
    fun missingFullTankBaselineIsInsufficientEvenWithPartialRecords() {
        val result = calculate(
            listOf(
                record(id = 1, odometerKm = 1_000, litersX100 = 2_000, fullTank = false),
                record(id = 2, odometerKm = 1_500, litersX100 = 4_000, fullTank = true),
            ),
        )

        assertEquals(FuelConsumptionResult.InsufficientData, result)
    }

    private fun record(
        id: Long,
        timestampMs: Long = id,
        odometerKm: Long,
        litersX100: Int,
        fullTank: Boolean,
    ) = FuelRecord(
        id = id,
        vehicleId = 1,
        timestampMs = timestampMs,
        odometerKm = odometerKm,
        litersX100 = litersX100,
        pricePerLiterCents = 180,
        totalCostCents = 0,
        isFullTank = fullTank,
        stationName = null,
        notes = null,
    )
}
