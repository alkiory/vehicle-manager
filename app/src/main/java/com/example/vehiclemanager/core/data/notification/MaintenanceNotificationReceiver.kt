package com.example.vehiclemanager.core.data.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver to handle notification actions (dismiss).
 */
class MaintenanceNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DISMISS = "com.example.vehiclemanager.ACTION_DISMISS_NOTIFICATION"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DISMISS -> {
                val notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, -1L)
                if (notificationId != -1L) {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId.toInt())
                }
            }
        }
    }
}
