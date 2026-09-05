package com.example.vehiclemanager.core.domain

import java.time.Instant
import java.time.ZoneOffset

class GetUpcomingServicesUseCase(
    private val dueSoonDistanceKm: Long = DEFAULT_DUE_SOON_DISTANCE_KM,
    private val dueSoonMonths: Long = DEFAULT_DUE_SOON_MONTHS,
) {
    init {
        require(dueSoonDistanceKm >= 0) { "dueSoonDistanceKm must not be negative" }
        require(dueSoonMonths >= 0) { "dueSoonMonths must not be negative" }
    }

    operator fun invoke(
        schedules: List<MaintenanceSchedule>,
        currentOdometerKm: Long,
        currentDateMs: Long,
    ): List<UpcomingService> = schedules.map { schedule ->
        evaluate(schedule, currentOdometerKm, currentDateMs)
    }

    private fun evaluate(
        schedule: MaintenanceSchedule,
        currentOdometerKm: Long,
        currentDateMs: Long,
    ): UpcomingService {
        val distanceDueKm = schedule.intervalKm
            ?.takeIf { it > 0 }
            ?.let { interval -> schedule.lastPerformedKm?.let { it + interval } }
        val dateDueMs = schedule.intervalMonths
            ?.takeIf { it > 0 }
            ?.let { interval -> schedule.lastPerformedDateMs?.plusMonthsSafely(interval.toLong()) }

        val distanceOverdue = distanceDueKm?.let { currentOdometerKm >= it } == true
        val dateOverdue = dateDueMs?.let { currentDateMs >= it } == true
        val distanceDueSoon = distanceDueKm?.let {
            !distanceOverdue && currentOdometerKm + dueSoonDistanceKm >= it
        } == true
        val dateDueSoon = dateDueMs?.let {
            !dateOverdue && currentDateMs >= it.minusMonthsSafely(dueSoonMonths)
        } == true

        val status = when {
            distanceOverdue || dateOverdue -> ServiceStatus.OVERDUE
            distanceDueSoon || dateDueSoon -> ServiceStatus.DUE_SOON
            else -> ServiceStatus.OK
        }

        return UpcomingService(
            schedule = schedule,
            status = status,
            distanceDueKm = distanceDueKm,
            dateDueMs = dateDueMs,
        )
    }

    private companion object {
        const val DEFAULT_DUE_SOON_DISTANCE_KM = 500L
        const val DEFAULT_DUE_SOON_MONTHS = 1L
    }
}

data class UpcomingService(
    val schedule: MaintenanceSchedule,
    val status: ServiceStatus,
    val distanceDueKm: Long?,
    val dateDueMs: Long?,
)

enum class ServiceStatus {
    DUE_SOON,
    OVERDUE,
    OK,
}

private fun Long.plusMonthsSafely(months: Long): Long = runCatching {
    Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .plusMonths(months)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}.getOrElse { Long.MAX_VALUE }

private fun Long.minusMonthsSafely(months: Long): Long = runCatching {
    Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .minusMonths(months)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}.getOrElse { Long.MIN_VALUE }
