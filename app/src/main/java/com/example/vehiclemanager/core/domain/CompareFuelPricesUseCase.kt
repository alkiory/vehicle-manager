package com.example.vehiclemanager.core.domain

import java.time.YearMonth
import javax.inject.Inject

/**
 * Use case for comparing fuel prices across stations and time periods.
 */
class CompareFuelPricesUseCase @Inject constructor() {

    /**
     * Compares fuel prices across stations.
     */
    operator fun invoke(
        fuelRecords: List<FuelRecord>,
        currentDateMs: Long,
    ): FuelPriceComparison {
        val byStation = fuelRecords
            .filter { it.stationName != null && it.stationName.isNotBlank() }
            .groupBy { it.stationName!! }

        val stationAnalysis = byStation.map { (stationName, records) ->
            val totalLitersX100 = records.sumOf { it.litersX100.toLong() }
            val weightedPrice = if (totalLitersX100 > 0) {
                records.fold(0L) { acc, record ->
                    acc + (record.pricePerLiterCents * record.litersX100.toLong())
                } / totalLitersX100
            } else 0L

            StationPriceAnalysis(
                stationName = stationName,
                recordCount = records.size,
                avgPricePerLiterCents = weightedPrice,
                minPriceCents = records.minOfOrNull { it.pricePerLiterCents } ?: 0L,
                maxPriceCents = records.maxOfOrNull { it.pricePerLiterCents } ?: 0L,
                totalLiters = (totalLitersX100 / 100).toInt(),
                totalCostCents = records.sumOf { it.totalCostCents },
                lastUpdateMs = records.maxOfOrNull { it.timestampMs } ?: 0L
            )
        }.sortedByDescending { it.avgPricePerLiterCents }

        val bestStation = stationAnalysis.minByOrNull { it.avgPricePerLiterCents }
        val worstStation = stationAnalysis.maxByOrNull { it.avgPricePerLiterCents }
        val averagePrice = if (stationAnalysis.isNotEmpty()) {
            stationAnalysis.sumOf { it.avgPricePerLiterCents } / stationAnalysis.size
        } else 0L

        val priceTrend = calculatePriceTrend(fuelRecords)

        val priceDiversity = if (stationAnalysis.size > 1) {
            val maxPrice = stationAnalysis.maxOfOrNull { it.avgPricePerLiterCents } ?: 0L
            val minPrice = stationAnalysis.minOfOrNull { it.avgPricePerLiterCents } ?: 0L
            if (minPrice > 0) (maxPrice - minPrice).toDouble() / minPrice else 0.0
        } else 0.0

        return FuelPriceComparison(
            totalStations = stationAnalysis.size,
            totalRecords = fuelRecords.size,
            averagePricePerLiterCents = averagePrice,
            bestStation = bestStation,
            worstStation = worstStation,
            stationAnalysis = stationAnalysis,
            priceTrend = priceTrend,
            priceDiversity = priceDiversity
        )
    }

    /**
     * Gets price history for charting.
     */
    fun getPriceHistory(fuelRecords: List<FuelRecord>): List<MonthlyPricePoint> {
        val monthlyData = mutableMapOf<YearMonth, MutableList<FuelRecord>>()

        fuelRecords.forEach { record ->
            val month = record.timestampMs.toYearMonth()
            monthlyData.getOrPut(month) { mutableListOf() }.add(record)
        }

        return monthlyData.toSortedMap().map { (month, records) ->
            val totalLitersX100 = records.sumOf { it.litersX100.toLong() }
            val weightedPrice = if (totalLitersX100 > 0) {
                records.fold(0L) { acc, record ->
                    acc + (record.pricePerLiterCents * record.litersX100.toLong())
                } / totalLitersX100
            } else 0L

            MonthlyPricePoint(
                year = month.year,
                month = month.monthValue,
                avgPricePerLiterCents = weightedPrice,
                recordCount = records.size,
                totalLiters = (totalLitersX100 / 100).toInt()
            )
        }
    }

    /**
     * Gets price comparison by station for a specific time period.
     */
    fun getStationPricesInPeriod(
        fuelRecords: List<FuelRecord>,
        startDateMs: Long,
        endDateMs: Long,
    ): List<PeriodStationPrice> {
        val periodRecords = fuelRecords.filter { it.timestampMs in startDateMs..endDateMs }

        return periodRecords
            .filter { it.stationName != null && it.stationName.isNotBlank() }
            .groupBy { it.stationName!! }
            .map { (station, records) ->
                val totalLitersX100 = records.sumOf { it.litersX100.toLong() }
                val weightedPrice = if (totalLitersX100 > 0) {
                    records.fold(0L) { acc, record ->
                        acc + (record.pricePerLiterCents * record.litersX100.toLong())
                    } / totalLitersX100
                } else 0L

                PeriodStationPrice(
                    stationName = station,
                    avgPricePerLiterCents = weightedPrice,
                    minPriceCents = records.minOfOrNull { it.pricePerLiterCents } ?: 0L,
                    maxPriceCents = records.maxOfOrNull { it.pricePerLiterCents } ?: 0L,
                    recordCount = records.size
                )
            }.sortedBy { it.avgPricePerLiterCents }
    }

    private fun calculatePriceTrend(fuelRecords: List<FuelRecord>): List<PriceTrendPoint> {
        val monthlyData = mutableMapOf<YearMonth, MutableList<FuelRecord>>()

        fuelRecords.forEach { record ->
            val month = record.timestampMs.toYearMonth()
            monthlyData.getOrPut(month) { mutableListOf() }.add(record)
        }

        val sortedMonths = monthlyData.keys.sorted()
        var prevPrice: Long? = null

        return sortedMonths.map { month ->
            val records = monthlyData[month] ?: emptyList()
            val totalLitersX100 = records.sumOf { it.litersX100.toLong() }
            val avgPrice = if (totalLitersX100 > 0) {
                records.fold(0L) { acc, record ->
                    acc + (record.pricePerLiterCents * record.litersX100.toLong())
                } / totalLitersX100
            } else 0L

            val changePercent = prevPrice?.let { prev ->
                if (prev > 0) {
                    ((avgPrice - prev).toDouble() / prev * 100).toLong()
                } else 0L
            } ?: 0L

            prevPrice = avgPrice

            PriceTrendPoint(
                year = month.year,
                month = month.monthValue,
                avgPricePerLiterCents = avgPrice,
                changePercent = changePercent
            )
        }
    }

    private fun Long.toYearMonth(): YearMonth = YearMonth.from(
        java.time.Instant.ofEpochMilli(this).atZone(java.time.ZoneOffset.UTC)
    )
}

data class FuelPriceComparison(
    val totalStations: Int,
    val totalRecords: Int,
    val averagePricePerLiterCents: Long,
    val bestStation: StationPriceAnalysis?,
    val worstStation: StationPriceAnalysis?,
    val stationAnalysis: List<StationPriceAnalysis>,
    val priceTrend: List<PriceTrendPoint>,
    val priceDiversity: Double
)

data class StationPriceAnalysis(
    val stationName: String,
    val recordCount: Int,
    val avgPricePerLiterCents: Long,
    val minPriceCents: Long,
    val maxPriceCents: Long,
    val totalLiters: Int,
    val totalCostCents: Long,
    val lastUpdateMs: Long
)

data class MonthlyPricePoint(
    val year: Int,
    val month: Int,
    val avgPricePerLiterCents: Long,
    val recordCount: Int,
    val totalLiters: Int
)

data class PriceTrendPoint(
    val year: Int,
    val month: Int,
    val avgPricePerLiterCents: Long,
    val changePercent: Long
)

data class PeriodStationPrice(
    val stationName: String,
    val avgPricePerLiterCents: Long,
    val minPriceCents: Long,
    val maxPriceCents: Long,
    val recordCount: Int
)
