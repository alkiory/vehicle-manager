package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceSchedule

fun MaintenanceScheduleEntity.toDomain() = MaintenanceSchedule(
    id = id,
    vehicleId = vehicleId,
    serviceTitle = serviceTitle,
    intervalKm = intervalKm,
    intervalMonths = intervalMonths,
    lastPerformedKm = lastPerformedKm,
    lastPerformedDateMs = lastPerformedDateMs,
)

fun MaintenanceSchedule.toEntity() = MaintenanceScheduleEntity(
    id = id,
    vehicleId = vehicleId,
    serviceTitle = serviceTitle,
    intervalKm = intervalKm,
    intervalMonths = intervalMonths,
    lastPerformedKm = lastPerformedKm,
    lastPerformedDateMs = lastPerformedDateMs,
)
