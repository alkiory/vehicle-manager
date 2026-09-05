package com.example.vehiclemanager.core.data.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceRecord

fun MaintenanceRecordEntity.toDomain(): MaintenanceRecord = MaintenanceRecord(
    id = id,
    vehicleId = vehicleId,
    title = title,
    category = category,
    costCents = costCents,
    odometerKm = odometerKm,
    timestampMs = timestampMs,
    notes = notes,
    performedBy = performedBy,
)

fun MaintenanceRecord.toEntity(): MaintenanceRecordEntity = MaintenanceRecordEntity(
    id = id,
    vehicleId = vehicleId,
    title = title,
    category = category,
    costCents = costCents,
    odometerKm = odometerKm,
    timestampMs = timestampMs,
    notes = notes,
    performedBy = performedBy,
)
