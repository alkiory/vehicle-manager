package com.example.vehiclemanager.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class GetUpcomingServicesUseCaseTest {
    private val useCase = GetUpcomingServicesUseCase(
        dueSoonDistanceKm = 500,
        dueSoonMonths = 1,
    )

    @Test
    fun distanceScheduleIsOkUntilDueSoonAndOverdue() {
        val schedule = schedule(intervalKm = 10_000, lastPerformedKm = 20_000)

        assertEquals(ServiceStatus.OK, useCase(listOf(schedule), 29_000, DATE).single().status)
        assertEquals(ServiceStatus.DUE_SOON, useCase(listOf(schedule), 29_500, DATE).single().status)
        assertEquals(ServiceStatus.OVERDUE, useCase(listOf(schedule), 30_000, DATE).single().status)
    }

    @Test
    fun timeScheduleUsesCalendarMonths() {
        val schedule = schedule(
            intervalMonths = 6,
            lastPerformedDateMs = epoch("2025-01-15"),
            intervalKm = null,
            lastPerformedKm = null,
        )

        assertEquals(ServiceStatus.OK, useCase(listOf(schedule), 0, epoch("2025-06-14")).single().status)
        assertEquals(ServiceStatus.DUE_SOON, useCase(listOf(schedule), 0, epoch("2025-06-15")).single().status)
        assertEquals(ServiceStatus.OVERDUE, useCase(listOf(schedule), 0, epoch("2025-07-15")).single().status)
    }

    @Test
    fun eitherDueDimensionMakesCombinedScheduleUrgent() {
        val schedule = schedule(
            intervalKm = 10_000,
            lastPerformedKm = 20_000,
            intervalMonths = 12,
            lastPerformedDateMs = epoch("2025-01-01"),
        )

        val result = useCase(listOf(schedule), 20_100, epoch("2026-01-01")).single()

        assertEquals(ServiceStatus.OVERDUE, result.status)
        assertEquals(30_000L, result.distanceDueKm)
        assertEquals(epoch("2026-01-01"), result.dateDueMs)
    }

    @Test
    fun missingBaselineDisablesThatDimension() {
        val distanceOnly = schedule(intervalKm = 10_000, lastPerformedKm = null)
        val timeOnly = schedule(
            intervalKm = null,
            lastPerformedKm = null,
            intervalMonths = 6,
            lastPerformedDateMs = null,
        )

        assertEquals(
            listOf(ServiceStatus.OK, ServiceStatus.OK),
            useCase(listOf(distanceOnly, timeOnly), 100_000, epoch("2030-01-01")).map { it.status },
        )
    }

    @Test
    fun schedulesAreReturnedInInputOrder() {
        val first = schedule(id = 1, serviceTitle = "Oil")
        val second = schedule(id = 2, serviceTitle = "Brakes")

        assertEquals(listOf(1L, 2L), useCase(listOf(first, second), 0, DATE).map { it.schedule.id })
    }

    private fun schedule(
        id: Long = 1,
        serviceTitle: String = "Service",
        intervalKm: Long? = 10_000,
        intervalMonths: Int? = null,
        lastPerformedKm: Long? = 20_000,
        lastPerformedDateMs: Long? = null,
    ) = MaintenanceSchedule(
        id = id,
        vehicleId = 1,
        serviceTitle = serviceTitle,
        intervalKm = intervalKm,
        intervalMonths = intervalMonths,
        lastPerformedKm = lastPerformedKm,
        lastPerformedDateMs = lastPerformedDateMs,
    )

    private fun epoch(date: String): Long = java.time.LocalDate.parse(date)
        .atStartOfDay(java.time.ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    private companion object {
        const val DATE = 0L
    }
}
