package com.example.vehiclemanager.core.domain

import javax.inject.Inject

/**
 * Unified timeline event representing either a fuel record or maintenance record.
 */
sealed class TimelineEvent {
    abstract val timestampMs: Long
    abstract val id: Long

    data class FuelRecordEvent(
        val record: FuelRecord,
    ) : TimelineEvent() {
        override val timestampMs: Long = record.timestampMs
        override val id: Long = record.id
    }

    data class MaintenanceRecordEvent(
        val record: MaintenanceRecord,
    ) : TimelineEvent() {
        override val timestampMs: Long = record.timestampMs
        override val id: Long = record.id
    }
}

/**
 * Data class representing a month separator in the timeline.
 */
data class TimelineMonth(
    val year: Int,
    val month: Int,
    val totalSpentCents: Long = 0L,
) {
    val label: String
        get() {
            if (month < 1 || month > 12) return "$year"
            val monthNames = arrayOf(
                "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
            )
            return try {
                "${monthNames[month - 1]} $year"
            } catch (e: IndexOutOfBoundsException) {
                "$year"
            }
        }
}

/**
 * Use case to get a unified chronological timeline of fuel and maintenance events.
 */
class GetUnifiedTimelineUseCase @Inject constructor() {
    /**
     * Returns a unified list of timeline events sorted by timestamp (most recent first).
     */
    operator fun invoke(
        fuelRecords: List<FuelRecord>,
        maintenanceRecords: List<MaintenanceRecord>,
    ): List<TimelineEvent> {
        val events = mutableListOf<TimelineEvent>()

        fuelRecords.forEach { record ->
            events.add(TimelineEvent.FuelRecordEvent(record))
        }

        maintenanceRecords.forEach { record ->
            events.add(TimelineEvent.MaintenanceRecordEvent(record))
        }

        return events.sortedWith(
            compareByDescending<TimelineEvent> { it.timestampMs }
                .thenBy { it.id }
        )
    }

    /**
     * Groups timeline events by month and calculates spending totals.
     */
    fun groupByMonth(events: List<TimelineEvent>): List<Pair<TimelineMonth, List<TimelineEvent>>> {
        if (events.isEmpty()) return emptyList()

        val calendar = java.util.Calendar.getInstance()

        val grouped = events.groupBy { event ->
            calendar.timeInMillis = event.timestampMs
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH) + 1 // 1-based
            Pair(year, month)
        }

        return grouped.map { (yearMonth, monthEvents) ->
            val (year, month) = yearMonth
            val totalSpentCents = monthEvents.sumOf { event ->
                when (event) {
                    is TimelineEvent.FuelRecordEvent -> event.record.totalCostCents
                    is TimelineEvent.MaintenanceRecordEvent -> event.record.costCents
                }
            }
            Pair(
                TimelineMonth(year, month, totalSpentCents),
                monthEvents
            )
        }.sortedWith(compareByDescending<Pair<TimelineMonth, List<TimelineEvent>>> { it.first.year }.thenBy { it.first.month })
    }
}
