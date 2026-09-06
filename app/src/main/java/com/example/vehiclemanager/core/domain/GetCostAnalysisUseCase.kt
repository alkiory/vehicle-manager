package com.example.vehiclemanager.core.domain

import java.time.YearMonth
import javax.inject.Inject

/**
 * Use case for analyzing costs by vehicle, period, and category.
 *
 * Provides detailed breakdowns of fuel and maintenance costs
 * with support for filtering and comparison.
 */
class GetCostAnalysisUseCase @Inject constructor() {

    private fun Long.isInPeriod(startDateMs: Long?, endDateMs: Long): Boolean =
        (startDateMs == null || this >= startDateMs) && this <= endDateMs

    private fun Long.toYearMonth(): YearMonth = YearMonth.from(
        java.time.Instant.ofEpochMilli(this).atZone(java.time.ZoneOffset.UTC)
    )

    /**
     * Analyzes costs across vehicles with filtering by period and category.
     */
    operator fun invoke(
        fuelRecords: List<FuelRecord>,
        maintenanceRecords: List<MaintenanceRecord>,
        vehicles: List<Vehicle>,
        period: StatsPeriod,
        currentDateMs: Long,
    ): CostAnalysis {
        val startDateMs = period.startDateMs(currentDateMs)

        val filteredFuel = fuelRecords.filter { it.timestampMs.isInPeriod(startDateMs, currentDateMs) }
        val filteredMaintenance = maintenanceRecords.filter { it.timestampMs.isInPeriod(startDateMs, currentDateMs) }

        // Group costs by vehicle
        val vehicleCostBreakdown = vehicles.associate { vehicle ->
            val vehicleFuel = filteredFuel.filter { it.vehicleId == vehicle.id }
            val vehicleMaintenance = filteredMaintenance.filter { it.vehicleId == vehicle.id }
            val fuelTotal = vehicleFuel.sumOf { it.totalCostCents }
            val maintTotal = vehicleMaintenance.sumOf { it.costCents }
            val total = fuelTotal + maintTotal
            val totalRecords = vehicleFuel.size + vehicleMaintenance.size

            vehicle.id to VehicleCostBreakdown(
                vehicleName = vehicle.name,
                fuelCostCents = fuelTotal,
                maintenanceCostCents = maintTotal,
                totalCostCents = total,
                fuelRecordsCount = vehicleFuel.size,
                maintenanceRecordsCount = vehicleMaintenance.size,
                avgCostPerRecord = if (totalRecords > 0) total / totalRecords else 0L
            )
        }

        // Group maintenance costs by category
        val maintenanceByCategory = filteredMaintenance.groupBy { it.category }
        val categoryBreakdown = maintenanceByCategory.map { (category, records) ->
            val total = records.sumOf { it.costCents }
            CategoryCostBreakdown(
                category = category,
                totalCostCents = total,
                recordCount = records.size,
                avgCostPerRecord = if (records.isNotEmpty()) total / records.size else 0L
            )
        }.sortedByDescending { it.totalCostCents }

        // Calculate totals and percentages
        val totalFuelCost = filteredFuel.sumOf { it.totalCostCents }
        val totalMaintenanceCost = filteredMaintenance.sumOf { it.costCents }
        val grandTotal = totalFuelCost + totalMaintenanceCost

        val fuelPercentage = if (grandTotal > 0) {
            (totalFuelCost.toDouble() / grandTotal * 100).toLong()
        } else 0L

        val maintenancePercentage = if (grandTotal > 0) {
            (totalMaintenanceCost.toDouble() / grandTotal * 100).toLong()
        } else 0L

        return CostAnalysis(
            period = period,
            periodStartDateMs = startDateMs,
            periodEndDateMs = currentDateMs,
            totalFuelCostCents = totalFuelCost,
            totalMaintenanceCostCents = totalMaintenanceCost,
            grandTotalCents = grandTotal,
            fuelPercentage = fuelPercentage,
            maintenancePercentage = maintenancePercentage,
            vehicleBreakdown = vehicleCostBreakdown.values.toList(),
            categoryBreakdown = categoryBreakdown,
            fuelRecordsCount = filteredFuel.size,
            maintenanceRecordsCount = filteredMaintenance.size
        )
    }

    /**
     * Compares costs between multiple vehicles.
     */
    fun compareVehicles(
        fuelRecords: List<FuelRecord>,
        maintenanceRecords: List<MaintenanceRecord>,
        vehicles: List<Vehicle>,
        period: StatsPeriod,
        currentDateMs: Long,
    ): List<VehicleCostComparison> {
        val startDateMs = period.startDateMs(currentDateMs)

        return vehicles.map { vehicle ->
            val vehicleFuel = fuelRecords.filter { it.vehicleId == vehicle.id }
                .filter { it.timestampMs.isInPeriod(startDateMs, currentDateMs) }
            val vehicleMaintenance = maintenanceRecords.filter { it.vehicleId == vehicle.id }
                .filter { it.timestampMs.isInPeriod(startDateMs, currentDateMs) }

            val totalFuel = vehicleFuel.sumOf { it.totalCostCents }
            val totalMaintenance = vehicleMaintenance.sumOf { it.costCents }
            val total = totalFuel + totalMaintenance
            val avgDistance = calculateAverageDistance(vehicleFuel, vehicleMaintenance)

            VehicleCostComparison(
                vehicleId = vehicle.id,
                vehicleName = vehicle.name,
                make = vehicle.make,
                model = vehicle.model,
                fuelCostCents = totalFuel,
                maintenanceCostCents = totalMaintenance,
                totalCostCents = total,
                averageDistanceKm = avgDistance,
                costPerKmCentsX100 = if (total > 0 && avgDistance > 0) {
                    (total * 100 / avgDistance)
                } else 0L,
                fuelRecordsCount = vehicleFuel.size,
                maintenanceRecordsCount = vehicleMaintenance.size
            )
        }.sortedByDescending { it.totalCostCents }
    }

    private fun calculateAverageDistance(
        fuelRecords: List<FuelRecord>,
        maintenanceRecords: List<MaintenanceRecord>
    ): Long {
        val allOdometers = (fuelRecords.map { it.odometerKm } + maintenanceRecords.map { it.odometerKm })
        return if (allOdometers.isEmpty()) {
            0L
        } else {
            allOdometers.maxOrNull()!! - allOdometers.minOrNull()!!
        }.coerceAtLeast(0L)
    }
}

data class CostAnalysis(
    val period: StatsPeriod,
    val periodStartDateMs: Long?,
    val periodEndDateMs: Long,
    val totalFuelCostCents: Long,
    val totalMaintenanceCostCents: Long,
    val grandTotalCents: Long,
    val fuelPercentage: Long,
    val maintenancePercentage: Long,
    val vehicleBreakdown: List<VehicleCostBreakdown>,
    val categoryBreakdown: List<CategoryCostBreakdown>,
    val fuelRecordsCount: Int,
    val maintenanceRecordsCount: Int
)

data class VehicleCostBreakdown(
    val vehicleName: String,
    val fuelCostCents: Long,
    val maintenanceCostCents: Long,
    val totalCostCents: Long,
    val fuelRecordsCount: Int,
    val maintenanceRecordsCount: Int,
    val avgCostPerRecord: Long
)

data class CategoryCostBreakdown(
    val category: MaintenanceCategory,
    val totalCostCents: Long,
    val recordCount: Int,
    val avgCostPerRecord: Long
)

data class VehicleCostComparison(
    val vehicleId: Long,
    val vehicleName: String,
    val make: String,
    val model: String,
    val fuelCostCents: Long,
    val maintenanceCostCents: Long,
    val totalCostCents: Long,
    val averageDistanceKm: Long,
    val costPerKmCentsX100: Long,
    val fuelRecordsCount: Int,
    val maintenanceRecordsCount: Int
)
