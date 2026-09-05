package com.example.vehiclemanager.feature.vehicles

import com.example.vehiclemanager.core.domain.FuelType

/** Validation messages are kept as stable keys so the UI can localize them later. */
data class VehicleValidationErrors(
    val name: String? = null,
    val make: String? = null,
    val model: String? = null,
    val year: String? = null,
    val licensePlate: String? = null,
    val odometer: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(name, make, model, year, licensePlate, odometer).any { it != null }
}

data class VehicleFormData(
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val licensePlate: String = "",
    val vin: String = "",
    val fuelType: FuelType = FuelType.PETROL,
    val primaryOdometerKm: String = "",
)

fun VehicleFormData.validate(currentYear: Int): VehicleValidationErrors {
    val parsedYear = year.toIntOrNull()
    val odometer = primaryOdometerKm.toLongOrNull()

    return VehicleValidationErrors(
        name = if (name.isBlank()) "required" else null,
        make = if (make.isBlank()) "required" else null,
        model = if (model.isBlank()) "required" else null,
        year = when {
            parsedYear == null -> "invalid"
            parsedYear !in MIN_VEHICLE_YEAR..currentYear -> "range"
            else -> null
        },
        licensePlate = if (licensePlate.isBlank()) "required" else null,
        odometer = when {
            odometer == null -> "invalid"
            odometer <= 0 -> "positive"
            else -> null
        },
    )
}

const val MIN_VEHICLE_YEAR = 1886
