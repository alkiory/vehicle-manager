package com.example.vehiclemanager.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for checking and triggering fuel price alerts.
 */
class FuelPriceAlertUseCase(
    private val fuelRecordRepository: FuelRecordRepository,
) {
    /**
     * Returns a flow of fuel price alerts that should be triggered.
     * Checks recent fuel prices against user's threshold preference.
     */
    fun getPriceAlerts(preferencesProvider: suspend () -> NotificationPreferences?): Flow<List<FuelPriceAlert>> {
        return fuelRecordRepository.observeRecentPrices().map { records ->
            val preferences = preferencesProvider()
            if (preferences?.fuelPriceAlertsEnabled != true) {
                return@map emptyList()
            }

            val threshold = preferences?.fuelPriceAlertThreshold ?: 0L
            if (threshold == 0L) {
                return@map emptyList()
            }

            records
                .filter { record ->
                    record.pricePerLiterCents <= threshold
                }
                .map { record ->
                    FuelPriceAlert(
                        recordId = record.id,
                        stationName = record.stationName ?: "Estación desconocida",
                        pricePerLiterCents = record.pricePerLiterCents,
                        vehicleId = record.vehicleId,
                        timestampMs = record.timestampMs,
                    )
                }
        }
    }

    /**
     * Update the fuel price alert threshold preference.
     */
    suspend fun updatePriceThreshold(
        thresholdCents: Long,
        preferencesRepository: com.example.vehiclemanager.core.data.notification.NotificationRepository,
    ) {
        val currentPrefs = preferencesRepository.getPreferences()
        preferencesRepository.updatePreferences(
            currentPrefs?.copy(fuelPriceAlertThreshold = thresholdCents)
                ?: NotificationPreferences(fuelPriceAlertThreshold = thresholdCents),
        )
    }

    /**
     * Enable or disable fuel price alerts.
     */
    suspend fun setFuelPriceAlertsEnabled(
        enabled: Boolean,
        preferencesRepository: com.example.vehiclemanager.core.data.notification.NotificationRepository,
    ) {
        val currentPrefs = preferencesRepository.getPreferences()
        preferencesRepository.updatePreferences(
            currentPrefs?.copy(fuelPriceAlertsEnabled = enabled)
                ?: NotificationPreferences(fuelPriceAlertsEnabled = enabled),
        )
    }
}

/**
 * Represents a fuel price alert that should be shown to the user.
 */
data class FuelPriceAlert(
    val recordId: Long,
    val stationName: String,
    val pricePerLiterCents: Long,
    val vehicleId: Long,
    val timestampMs: Long,
) {
    fun getPricePerLiteralEuros(): Double = pricePerLiterCents / 100.0
}
