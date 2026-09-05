package com.example.vehiclemanager.core.domain

data class MaintenanceRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val title: String,
    val category: MaintenanceCategory,
    val costCents: Long,
    val odometerKm: Long,
    val timestampMs: Long,
    val notes: String?,
    val performedBy: String?,
)
