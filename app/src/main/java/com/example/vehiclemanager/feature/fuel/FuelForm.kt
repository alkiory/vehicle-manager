package com.example.vehiclemanager.feature.fuel

import java.math.BigDecimal
import java.math.RoundingMode

/** Raw text state for the refueling form. */
data class FuelFormData(
    val timestampMs: Long = System.currentTimeMillis(),
    val odometerKm: String = "",
    val liters: String = "",
    val pricePerLiter: String = "",
    val isFullTank: Boolean = true,
    val stationName: String = "",
    val notes: String = "",
)

data class FuelValidationErrors(
    val odometer: String? = null,
    val liters: String? = null,
    val pricePerLiter: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(odometer, liters, pricePerLiter).any { it != null }
}

fun FuelFormData.validate(previousOdometerKm: Long?): FuelValidationErrors {
    val odometer = odometerKm.toLongOrNull()
    val liters = decimalOrNull(liters)
    val price = decimalOrNull(pricePerLiter)

    return FuelValidationErrors(
        odometer = when {
            odometer == null -> "invalid"
            odometer <= 0 -> "positive"
            previousOdometerKm != null && odometer < previousOdometerKm -> "lower_than_previous"
            else -> null
        },
        liters = when {
            liters == null -> "invalid"
            liters <= BigDecimal.ZERO -> "positive"
            else -> null
        },
        pricePerLiter = when {
            price == null -> "invalid"
            price <= BigDecimal.ZERO -> "positive"
            else -> null
        },
    )
}

fun calculatePricePerLiterCents(pricePerLiter: String): Long? {
    val priceValue = decimalOrNull(pricePerLiter) ?: return null
    if (priceValue <= BigDecimal.ZERO) return null

    return priceValue
        .movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
}

/**
 * Calculates persisted cents from decimal liters and price without using
 * floating-point arithmetic. Both inputs are converted to exact decimals first.
 */
fun calculateTotalCostCents(liters: String, pricePerLiter: String): Long? {
    val litersValue = decimalOrNull(liters) ?: return null
    val priceCents = calculatePricePerLiterCents(pricePerLiter) ?: return null
    if (litersValue <= BigDecimal.ZERO) return null

    return litersValue
        .multiply(BigDecimal.valueOf(priceCents))
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
}

fun litersToX100(liters: String): Int? {
    val litersValue = decimalOrNull(liters) ?: return null
    if (litersValue <= BigDecimal.ZERO) return null

    return litersValue
        .movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .intValueExact()
}

private fun decimalOrNull(value: String): BigDecimal? = value
    .trim()
    .takeIf(String::isNotEmpty)
    ?.toBigDecimalOrNull()
