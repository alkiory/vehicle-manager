package com.example.vehiclemanager.feature.fuel

import java.math.BigDecimal
import java.math.RoundingMode

/** The three amount fields linked by Total = Liters x Price/L. */
enum class FuelAmountField { LITERS, PRICE_PER_LITER, TOTAL_COST }

/**
 * Returns the one field not present in the given set of user-edited fields,
 * or null when fewer/more than two fields were edited.
 */
internal fun Set<FuelAmountField>.missingAmountField(): FuelAmountField? =
    FuelAmountField.entries.singleOrNull { it !in this }

/** Raw text state for the refueling form. */
data class FuelFormData(
    val timestampMs: Long = System.currentTimeMillis(),
    val odometerKm: String = "",
    val liters: String = "",
    val pricePerLiter: String = "",
    val totalCost: String = "",
    val isFullTank: Boolean = true,
    val stationName: String = "",
    val notes: String = "",
    /** Field whose current value was computed automatically from the other two. */
    val autoFilledField: FuelAmountField? = null,
)

data class FuelValidationErrors(
    val odometer: String? = null,
    val liters: String? = null,
    val pricePerLiter: String? = null,
    val totalCost: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(odometer, liters, pricePerLiter, totalCost).any { it != null }
}

fun FuelFormData.validate(previousOdometerKm: Long?): FuelValidationErrors {
    val odometer = odometerKm.toLongOrNull()
    val liters = decimalOrNull(liters)
    val price = decimalOrNull(pricePerLiter)
    val total = decimalOrNull(totalCost)

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
        totalCost = when {
            total == null -> "invalid"
            total <= BigDecimal.ZERO -> "positive"
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

/** Parses the user-facing total-cost text into persisted minor units. */
fun calculateTotalCostCents(totalCost: String): Long? {
    val totalValue = decimalOrNull(totalCost) ?: return null
    if (totalValue <= BigDecimal.ZERO) return null

    return totalValue
        .movePointRight(2)
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

/**
 * Result of the dynamic calculator: the computed amount in persisted minor
 * units (liters x100 or cents) plus the field it belongs to.
 */
internal data class ComputedAmount(val valueMinor: Long, val field: FuelAmountField) {
    /** Formats the amount as a user-facing decimal text with two fraction digits. */
    fun toUserText(): String {
        val whole = valueMinor / 100
        val hundredths = (valueMinor % 100).toString().padStart(2, '0')
        return "$whole.$hundredths"
    }
}

/** Total / Price = Liters, with both inputs in decimal text. */
internal fun computeLiters(pricePerLiter: String, totalCost: String): ComputedAmount? {
    val priceCents = calculatePricePerLiterCents(pricePerLiter) ?: return null
    val totalCents = calculateTotalCostCents(totalCost) ?: return null

    val litersX100 = BigDecimal.valueOf(totalCents)
        .multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(priceCents), 0, RoundingMode.HALF_UP)
    if (litersX100 <= BigDecimal.ZERO) return null

    return ComputedAmount(litersX100.longValueExact(), FuelAmountField.LITERS)
}

/** Total / Liters = Price, with liters in decimal text. */
internal fun computePricePerLiterCents(liters: String, totalCost: String): ComputedAmount? {
    val litersX100 = litersToX100(liters) ?: return null
    val totalCents = calculateTotalCostCents(totalCost) ?: return null
    if (litersX100 <= 0) return null

    // totalCents / (litersX100 / 100) = totalCents * 100 / litersX100 cents.
    val priceCents = BigDecimal.valueOf(totalCents)
        .multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(litersX100.toLong()), 0, RoundingMode.HALF_UP)
    if (priceCents <= BigDecimal.ZERO) return null

    return ComputedAmount(priceCents.longValueExact(), FuelAmountField.PRICE_PER_LITER)
}

/** Liters x Price = Total, matching the persisted-minor-units calculations. */
internal fun computeTotalCostFromParts(liters: String, pricePerLiter: String): ComputedAmount? {
    val totalCents = calculateTotalCostCents(liters, pricePerLiter) ?: return null
    return ComputedAmount(totalCents, FuelAmountField.TOTAL_COST)
}

private fun decimalOrNull(value: String): BigDecimal? = value
    .trim()
    .takeIf(String::isNotEmpty)
    ?.toBigDecimalOrNull()
