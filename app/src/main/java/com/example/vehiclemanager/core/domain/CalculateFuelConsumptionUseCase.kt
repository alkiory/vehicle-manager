package com.example.vehiclemanager.core.domain

/** Result of calculating consumption from full-tank fuel records. */
sealed interface FuelConsumptionResult {
    data class Calculated(
        val litersPer100KmX100: Long,
        val intervalsUsed: Int,
    ) : FuelConsumptionResult

    data object InsufficientData : FuelConsumptionResult
}

/**
 * Calculates fuel consumption across consecutive full-tank checkpoints.
 *
 * Partial refuels between two full tanks are included in the interval total.
 * Records are ordered by timestamp and ID before calculation so callers do not
 * need to pre-sort repository results.
 */
class CalculateFuelConsumptionUseCase {
    operator fun invoke(records: List<FuelRecord>): FuelConsumptionResult {
        val orderedRecords = records.sortedWith(
            compareBy<FuelRecord> { it.timestampMs }.thenBy { it.id },
        )
        val fullTankIndexes = orderedRecords
            .mapIndexedNotNull { index, record -> index.takeIf { record.isFullTank } }

        if (fullTankIndexes.size < 2) return FuelConsumptionResult.InsufficientData

        var totalLitersX100 = 0L
        var totalDistanceKm = 0L
        var intervalsUsed = 0

        fullTankIndexes.zipWithNext().forEach { (startIndex, endIndex) ->
            val start = orderedRecords[startIndex]
            val end = orderedRecords[endIndex]
            val distanceKm = end.odometerKm - start.odometerKm
            if (distanceKm <= 0) return@forEach

            val intervalLitersX100 = orderedRecords
                .subList(startIndex + 1, endIndex + 1)
                .sumOf { it.litersX100.toLong() }
            if (intervalLitersX100 <= 0) return@forEach

            totalLitersX100 += intervalLitersX100
            totalDistanceKm += distanceKm
            intervalsUsed++
        }

        if (intervalsUsed == 0 || totalDistanceKm <= 0) {
            return FuelConsumptionResult.InsufficientData
        }

        return FuelConsumptionResult.Calculated(
            litersPer100KmX100 = (totalLitersX100 * 100L) / totalDistanceKm,
            intervalsUsed = intervalsUsed,
        )
    }
}
