package com.example.vehiclemanager.core.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.vehiclemanager.MainActivity
import com.example.vehiclemanager.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android-specific notification manager for displaying maintenance reminders.
 */
@Singleton
class NotificationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID_MAINTENANCE = "maintenance_reminders"
        private const val CHANNEL_NAME_MAINTENANCE = "Service Reminders"
        private const val CHANNEL_DESCRIPTION_MAINTENANCE = "Notifications for upcoming maintenance services"

        private const val CHANNEL_ID_FUEL_PRICE = "fuel_price_alerts"
        private const val CHANNEL_NAME_FUEL_PRICE = "Fuel Price Alerts"
        private const val CHANNEL_DESCRIPTION_FUEL_PRICE = "Notifications when fuel prices are favorable"

        private const val NOTIFICATION_ID_PREFIX = 1000L
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Maintenance reminders channel
            val maintenanceChannel = NotificationChannel(
                CHANNEL_ID_MAINTENANCE,
                CHANNEL_NAME_MAINTENANCE,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = CHANNEL_DESCRIPTION_MAINTENANCE
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(maintenanceChannel)

            // Fuel price alerts channel
            val fuelPriceChannel = NotificationChannel(
                CHANNEL_ID_FUEL_PRICE,
                CHANNEL_NAME_FUEL_PRICE,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = CHANNEL_DESCRIPTION_FUEL_PRICE
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(fuelPriceChannel)
        }
    }

    /**
     * Show a maintenance reminder notification.
     */
    fun showMaintenanceReminder(
        notificationId: Long,
        vehicleName: String,
        serviceTitle: String,
        dueDateMs: Long,
        dueOdometer: Long,
        isOverdue: Boolean,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigationDestination", "maintenance")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val dismissIntent = Intent(context, MaintenanceNotificationReceiver::class.java).apply {
            action = MaintenanceNotificationReceiver.ACTION_DISMISS
            putExtra("notification_id", notificationId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            (notificationId + 1).toInt(),
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val now = System.currentTimeMillis()
        val daysUntilDue = (dueDateMs - now) / (24 * 60 * 60 * 1000)
        val title = if (isOverdue) {
            context.getString(R.string.notification_service_overdue)
        } else if (daysUntilDue <= 7) {
            context.getString(R.string.notification_service_due_soon)
        } else {
            context.getString(R.string.notification_service_upcoming)
        }

        val contentText = buildString {
            append(vehicleName)
            append(" - ")
            append(serviceTitle)
            if (isOverdue) {
                append("\n")
                append(getDueInfo(dueOdometer, daysUntilDue))
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MAINTENANCE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(if (isOverdue) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDeleteIntent(dismissPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationId.toInt(), notification)
    }

    /**
     * Show a fuel price alert notification.
     */
    fun showFuelPriceAlert(
        notificationId: Long,
        stationName: String,
        pricePerLiter: Double,
        vehicleName: String? = null,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigationDestination", "fuel")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val contentTitle = context.getString(R.string.notification_fuel_price_alert)
        val contentText = buildString {
            if (vehicleName != null) {
                append(vehicleName)
                append(" - ")
            }
            append(stationName)
            append(" - ")
            append(String.format("%.2f", pricePerLiter))
            append(" €/L")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_FUEL_PRICE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationId.toInt(), notification)
    }

    /**
     * Cancel a notification by ID.
     */
    fun cancelNotification(notificationId: Long) {
        notificationManager.cancel(notificationId.toInt())
    }

    /**
     * Cancel all notifications for a vehicle.
     */
    fun cancelAllNotificationsForVehicle(vehicleId: Long) {
        // In production, this would iterate through known notification IDs for the vehicle
        // For now, we cancel all maintenance notifications
        notificationManager.cancelAll()
    }

    private fun getDueInfo(dueOdometer: Long, daysUntilDue: Long): String {
        return buildString {
            if (dueOdometer > 0) {
                append("A los ")
                append(dueOdometer.toInt())
                append(" km")
            }
            if (daysUntilDue > 0) {
                append(" - Dentro de ")
                append(daysUntilDue.toInt())
                append(" días")
            }
        }
    }
}
