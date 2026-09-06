package com.example.vehiclemanager.core.domain

import java.time.YearMonth
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Predicts future fuel consumption and costs based on historical data.
 *
 * Uses simple linear regression on monthly data to generate forecasts.
 * Predictions require at least 3 data points for reasonable accuracy.
 */
class PredictFuelTrendsUseCase @Inject constructor() {

    /**
     * Predicts fuel consumption and cost trends for the next N months.
     *
     * @param fuelRecords Historical fuel records
     * @param monthsToPredict Number of months to predict ahead (default: 3)
     * @return TrendPrediction containing historical data, predictions, and confidence metrics
     */
    operator fun invoke(
        fuelRecords: List<FuelRecord>,
        monthsToPredict: Int = 3,
    ): TrendPrediction {
        val monthlyData = buildMonthlyFuelData(fuelRecords)

        if (monthlyData.size < 3) {
            return TrendPrediction(
                historicalData = monthlyData,
                predictions = emptyList(),
                confidenceLevel = ConfidenceLevel.LOW,
                message = "Se necesitan al menos 3 meses de datos para hacer predicciones"
            )
        }

        // Perform linear regression on monthly consumption
        val consumptionData = monthlyData.map { it.monthIndex to it.totalLiters.toDouble() }
        val costData = monthlyData.map { it.monthIndex to it.totalCostCents.toDouble() }
        val consumptionTrend = linearRegression(consumptionData)
        val costTrend = linearRegression(costData)

        // Generate predictions for next N months
        val predictions = generatePredictions(
            monthlyData = monthlyData,
            consumptionTrend = consumptionTrend,
            costTrend = costTrend,
            monthsToPredict = monthsToPredict
        )

        // Calculate confidence based on R-squared values
        val confidenceLevel = calculateConfidence(
            consumptionR2 = consumptionTrend.rSquared,
            costR2 = costTrend.rSquared,
            dataPoints = monthlyData.size
        )

        return TrendPrediction(
            historicalData = monthlyData,
            predictions = predictions,
            confidenceLevel = confidenceLevel,
            message = when {
                confidenceLevel == ConfidenceLevel.HIGH -> "Predicción altamente confiable"
                confidenceLevel == ConfidenceLevel.MEDIUM -> "Predicción moderadamente confiable"
                else -> "Predicción con baja confianza - más datos mejorarán la precisión"
            }
        )
    }

    private fun buildMonthlyFuelData(fuelRecords: List<FuelRecord>): List<HistoricalMonthData> {
        val monthlyMap = mutableMapOf<YearMonth, MutableList<FuelRecord>>()

        fuelRecords.forEach { record ->
            val month = record.timestampMs.toYearMonth()
            monthlyMap.getOrPut(month) { mutableListOf() }.add(record)
        }

        return monthlyMap.keys.sorted().mapIndexed { index, month ->
            val records = monthlyMap[month] ?: emptyList()
            val totalLitersX100 = records.sumOf { it.litersX100.toLong() }
            val totalLiters = totalLitersX100 / 100.0
            val totalCost = records.sumOf { it.totalCostCents }

            HistoricalMonthData(
                year = month.year,
                month = month.monthValue,
                totalLiters = (totalLiters).toInt(),
                totalCostCents = totalCost,
                recordCount = records.size,
                avgPricePerLiter = if (totalLiters > 0) {
                    (totalCost / (totalLiters * 100)).toLong()
                } else 0L,
                monthIndex = index
            )
        }
    }

    private data class LinearTrend(
        val slope: Double,
        val intercept: Double,
        val rSquared: Double
    )

    private fun linearRegression(data: List<Pair<Int, Double>>): LinearTrend {
        if (data.size < 2) {
            return LinearTrend(0.0, 0.0, 0.0)
        }

        val n = data.size.toDouble()
        val sumX = data.sumOf { it.first.toDouble() }
        val sumY = data.sumOf { it.second }
        val sumXY = data.sumOf { it.first.toDouble() * it.second }
        val sumX2 = data.sumOf { it.first.toDouble() * it.first.toDouble() }
        val sumY2 = data.sumOf { it.second * it.second }

        val slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        val intercept = (sumY - slope * sumX) / n

        // Calculate R-squared
        val meanY = sumY / n
        val ssTot = data.sumOf { it.second * it.second } - n * meanY * meanY
        val ssRes = data.sumOf { Math.pow(it.second - (slope * it.first + intercept), 2.0) }

        val rSquared = if (ssTot == 0.0) {
            1.0
        } else {
            ((ssTot - ssRes) / ssTot).coerceIn(0.0, 1.0)
        }

        return LinearTrend(slope, intercept, rSquared)
    }

    private fun generatePredictions(
        monthlyData: List<HistoricalMonthData>,
        consumptionTrend: LinearTrend,
        costTrend: LinearTrend,
        monthsToPredict: Int
    ): List<MonthPrediction> {
        val lastMonthIndex = monthlyData.lastOrNull()?.let { it.month to it.year }?.let { (m, y) ->
            (y * 12 + m) - 1
        } ?: 0

        return (1..monthsToPredict).map { i ->
            val nextMonthIndex = lastMonthIndex + i
            val predictedLiters = (consumptionTrend.slope * nextMonthIndex + consumptionTrend.intercept).coerceAtLeast(0.0)
            val predictedCost = (costTrend.slope * nextMonthIndex + costTrend.intercept).coerceAtLeast(0.0)

            MonthPrediction(
                monthOffset = i,
                predictedLiters = predictedLiters.toInt(),
                predictedCostCents = predictedCost.toLong(),
                confidence = calculatePredictionConfidence(consumptionTrend.rSquared, costTrend.rSquared, i)
            )
        }
    }

    private fun calculatePredictionConfidence(
        consumptionR2: Double,
        costR2: Double,
        monthOffset: Int
    ): Double {
        // Confidence decreases with time horizon and increases with R-squared
        val baseConfidence = (consumptionR2 + costR2) / 2
        val decayFactor = Math.pow(0.9, monthOffset.toDouble()) // 10% decay per month
        return (baseConfidence * decayFactor).coerceIn(0.0, 1.0)
    }

    private fun calculateConfidence(
        consumptionR2: Double,
        costR2: Double,
        dataPoints: Int
    ): ConfidenceLevel {
        val avgR2 = (consumptionR2 + costR2) / 2

        return when {
            avgR2 >= 0.8 && dataPoints >= 6 -> ConfidenceLevel.HIGH
            avgR2 >= 0.5 || dataPoints >= 4 -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }
    }

    private fun Long.toYearMonth(): YearMonth = YearMonth.from(
        java.time.Instant.ofEpochMilli(this).atZone(java.time.ZoneOffset.UTC)
    )

}

/**
 * Result of fuel trend prediction analysis.
 */
data class TrendPrediction(
    val historicalData: List<HistoricalMonthData>,
    val predictions: List<MonthPrediction>,
    val confidenceLevel: ConfidenceLevel,
    val message: String
)

/**
 * Historical monthly fuel data for trend analysis.
 */
data class HistoricalMonthData(
    val year: Int,
    val month: Int,
    val totalLiters: Int,
    val totalCostCents: Long,
    val recordCount: Int,
    val avgPricePerLiter: Long,
    val monthIndex: Int = 0
)

/**
 * Predicted values for a future month.
 */
data class MonthPrediction(
    val monthOffset: Int,
    val predictedLiters: Int,
    val predictedCostCents: Long,
    val confidence: Double
)

/**
 * Confidence level for predictions.
 */
enum class ConfidenceLevel {
    LOW,
    MEDIUM,
    HIGH
}
