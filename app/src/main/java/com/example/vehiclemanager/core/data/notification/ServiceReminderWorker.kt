package com.example.vehiclemanager.core.data.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.vehiclemanager.core.domain.GetDueServicesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that checks for due services and schedules notifications.
 */
@HiltWorker
class ServiceReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getDueServicesUseCase: GetDueServicesUseCase,
    private val notificationManager: NotificationManagerImpl,
    private val notificationRepository: NotificationRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val dueServices = getDueServicesUseCase.getDueServices().first()

            dueServices.forEach { service ->
                // Check if we've already triggered a notification for this service
                val existingNotification = notificationRepository.getNotification(service.scheduleId)
                if (existingNotification == null || !existingNotification.isTriggered) {
                    // Schedule notification
                    val notificationId = SERVICE_NOTIFICATION_PREFIX + service.scheduleId
                    notificationManager.showMaintenanceReminder(
                        notificationId = notificationId,
                        vehicleName = service.vehicleName,
                        serviceTitle = service.serviceTitle,
                        dueDateMs = service.dueDateMs,
                        dueOdometer = service.dueOdometer,
                        isOverdue = service.status == com.example.vehiclemanager.core.domain.ServiceStatus.OVERDUE,
                    )

                    // Save notification record
                    val notificationEntity = NotificationScheduleEntity(
                        id = service.scheduleId,
                        vehicleId = service.vehicleId,
                        serviceTitle = service.serviceTitle,
                        scheduledDateMs = service.dueDateMs,
                        isTriggered = true,
                        triggeredDateMs = System.currentTimeMillis(),
                        maintenanceScheduleId = service.scheduleId,
                    )
                    notificationRepository.insertNotification(notificationEntity)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "service_reminder_check"
        private const val SERVICE_NOTIFICATION_PREFIX = 10000L

        /**
         * Schedule periodic notification checks.
         * Runs every 12 hours to check for due services.
         */
        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ServiceReminderWorker>(
                12, TimeUnit.HOURS,
            ).build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest,
                )
        }

        /**
         * Cancel periodic notification checks.
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
