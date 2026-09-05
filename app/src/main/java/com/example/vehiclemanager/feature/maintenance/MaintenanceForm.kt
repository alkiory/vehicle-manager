package com.example.vehiclemanager.feature.maintenance

import java.math.BigDecimal
import java.math.RoundingMode

/** Raw text state for the maintenance entry form. */
data class MaintenanceFormData(
    val title: String = "",
    val category: com.example.vehiclemanager.core.domain.MaintenanceCategory =
        com.example.vehiclemanager.core.domain.MaintenanceCategory.OTHER,
    val cost: String = "",
    val odometerKm: String = "",
    val timestampMs: Long = System.currentTimeMillis(),
    val notes: String = "",
    val performedBy: String = "",
)

data class MaintenanceValidationErrors(
    val title: String? = null,
    val cost: String? = null,
    val odometer: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(title, cost, odometer).any { it != null }
}

fun MaintenanceFormData.validate(previousOdometerKm: Long?): MaintenanceValidationErrors {
    val costValue = cost.trim().toBigDecimalOrNull()
    val odometer = odometerKm.trim().toLongOrNull()

    return MaintenanceValidationErrors(
        title = if (title.isBlank()) "required" else null,
        cost = when {
            costValue == null -> "invalid"
            costValue < BigDecimal.ZERO -> "positive"
            else -> null
        },
        odometer = when {
            odometer == null -> "invalid"
            odometer <= 0 -> "positive"
            previousOdometerKm != null && odometer < previousOdometerKm -> "lower_than_previous"
            else -> null
        },
    )
}

fun maintenanceCostToCents(cost: String): Long? {
    val value = cost.trim().toBigDecimalOrNull() ?: return null
    if (value < BigDecimal.ZERO) return null
    return value.movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
}

fun odometerAfterMaintenance(currentOdometerKm: Long, serviceOdometerKm: Long): Long =
    maxOf(currentOdometerKm, serviceOdometerKm)
