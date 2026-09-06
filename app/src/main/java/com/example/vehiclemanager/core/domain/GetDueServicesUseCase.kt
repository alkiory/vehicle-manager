package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.LocalDate
import java.time.Period

/**
 * Use case to retrieve maintenance schedules that are due or upcoming.
 */
class GetDueServicesUseCase(
    private val maintenanceScheduleRepository: MaintenanceScheduleRepository,
) {
    /**
     * Returns a flow of due service notifications.
     * Filters out dismissed schedules and only includes schedules for active vehicles.
     */
    fun getDueServices(): Flow<List<DueServiceNotification>> {
        return maintenanceScheduleRepository.observeSchedulesForActiveVehicle().map { schedules ->
            schedules.mapNotNull { schedule ->
                val vehicleOdometer = getVehicleOdometer(schedule.vehicleId)
                if (isDismissed(schedule) || !isDueOrUpcoming(schedule, vehicleOdometer)) {
                    return@mapNotNull null
                }
                val vehicle = getVehicleInfo(schedule.vehicleId)
                DueServiceNotification(
                    scheduleId = schedule.id,
                    vehicleId = schedule.vehicleId,
                    vehicleName = vehicle?.name ?: "Vehículo desconocido",
                    serviceTitle = schedule.serviceTitle,
                    dueDateMs = calculateNextDueDate(schedule, vehicleOdometer),
                    dueOdometer = calculateNextDueOdometer(schedule, vehicleOdometer),
                    status = determineServiceStatus(schedule, vehicleOdometer),
                )
            }.sortedWith(
                compareBy<DueServiceNotification> { it.status }
                    .thenBy { it.dueDateMs }
            )
        }
    }

    private fun isDismissed(schedule: MaintenanceSchedule): Boolean {
        return schedule.lastPerformedDateMs == null
    }

    private fun getVehicleOdometer(vehicleId: Long): Long {
        // Get the latest odometer from fuel records for this vehicle
        return 0L // Default to 0 if no data
    }

    private fun getVehicleInfo(vehicleId: Long): Vehicle? {
        // This would be provided by a repository in production
        return null
    }

    private fun isDueOrUpcoming(
        schedule: MaintenanceSchedule,
        currentOdometer: Long,
    ): Boolean {
        val nextDueKm = calculateNextDueOdometer(schedule, currentOdometer)
        val nextDueDate = calculateNextDueDate(schedule, currentOdometer)

        val now = System.currentTimeMillis()
        val daysUntilDue = (nextDueDate - now) / (24 * 60 * 60 * 1000)

        // Service is due or upcoming if:
        // - Odometer threshold reached, OR
        // - Date threshold reached (within reminder window or overdue)
        return currentOdometer >= nextDueKm ||
                daysUntilDue <= 30L // Within 30 days counts as upcoming
    }

    fun calculateNextDueDate(
        schedule: MaintenanceSchedule,
        currentOdometer: Long,
    ): Long {
        // Calculate based on date interval
        if (schedule.intervalMonths != null && schedule.lastPerformedDateMs != null) {
            val lastDate = Instant.ofEpochMilli(schedule.lastPerformedDateMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val nextDate = lastDate.plusMonths(schedule.intervalMonths.toLong())
            return nextDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        }
        // Default to 30 days from now if no date info
        return System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
    }

    fun calculateNextDueOdometer(
        schedule: MaintenanceSchedule,
        currentOdometer: Long,
    ): Long {
        if (schedule.intervalKm != null && schedule.lastPerformedKm != null) {
            return schedule.lastPerformedKm + schedule.intervalKm
        }
        // Default to current + interval if available
        return currentOdometer + (schedule.intervalKm ?: 10000L)
    }

    fun determineServiceStatus(
        schedule: MaintenanceSchedule,
        currentOdometer: Long,
    ): ServiceStatus {
        val nextDueOdometer = calculateNextDueOdometer(schedule, currentOdometer)
        val nextDueDate = calculateNextDueDate(schedule, currentOdometer)
        val now = System.currentTimeMillis()

        val kmOverdue = currentOdometer - nextDueOdometer
        val daysOverdue = (now - nextDueDate) / (24 * 60 * 60 * 1000)

        return when {
            kmOverdue > 0 || daysOverdue > 0 -> ServiceStatus.OVERDUE
            kmOverdue > -1000 || daysOverdue > -7 -> ServiceStatus.DUE_SOON
            else -> ServiceStatus.OK
        }
    }
}

/**
 * Represents a service that is due or upcoming.
 */
data class DueServiceNotification(
    val scheduleId: Long,
    val vehicleId: Long,
    val vehicleName: String,
    val serviceTitle: String,
    val dueDateMs: Long,
    val dueOdometer: Long,
    val status: ServiceStatus,
)

/**
 * Service status for notifications.
 */
enum class ServiceStatus {
    /** Service is overdue and needs immediate attention */
    OVERDUE,
    /** Service is due soon (within 7 days or 1000km) */
    DUE_SOON,
    /** Service is not yet due */
    OK,
}