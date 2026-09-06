package com.example.vehiclemanager.core.domain

/**
 * User preferences for reminder notifications.
 */
data class ReminderPreferences(
    val advanceDistanceKm: Long = 500L,
    val advanceDays: Int = 7,
    val notificationTimeHours: Int = 9,
    val notificationTimeMinutes: Int = 0,
    val fuelNotificationsEnabled: Boolean = true,
    val tirePressureNotificationsEnabled: Boolean = false,
    val vibrateOnNotification: Boolean = true,
)

/**
 * Validation errors for reminder preferences.
 */
data class ReminderPreferencesValidationErrors(
    val advanceDistanceKm: String? = null,
    val advanceDays: String? = null,
    val notificationTimeHours: String? = null,
    val notificationTimeMinutes: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(advanceDistanceKm, advanceDays, notificationTimeHours, notificationTimeMinutes)
            .any { it != null }
}

/**
 * Validates reminder preferences inputs.
 */
fun ReminderPreferences.validate(): ReminderPreferencesValidationErrors {
    val distance = advanceDistanceKm
    val days = advanceDays
    val hours = notificationTimeHours
    val minutes = notificationTimeMinutes

    return ReminderPreferencesValidationErrors(
        advanceDistanceKm = when {
            distance < 0 -> "La distancia debe ser positiva"
            distance > 0 && distance < 10 -> "Mínimo 10 km"
            else -> null
        },
        advanceDays = when {
            days < 0 -> "Los días deben ser positivos"
            days > 0 && days < 1 -> "Mínimo 1 día"
            else -> null
        },
        notificationTimeHours = when {
            hours < 0 || hours > 23 -> "Hora inválida (0-23)"
            else -> null
        },
        notificationTimeMinutes = when {
            minutes < 0 || minutes > 59 -> "Minutos inválidos (0-59)"
            else -> null
        },
    )
}
