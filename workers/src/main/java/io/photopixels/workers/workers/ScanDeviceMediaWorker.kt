package io.photopixels.workers.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.photopixels.domain.usecases.GenerateMissingLocalHashes
import io.photopixels.domain.usecases.GetDeviceMediaUseCase
import io.photopixels.domain.usecases.UpdateDeviceMediaDataToDbUseCase
import io.photopixels.workers.R
import timber.log.Timber

/**
 * Android worker for reading device local media files and store the information about them in App's Database
 */
@HiltWorker
class ScanDeviceMediaWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updateDeviceMediaUseCase: UpdateDeviceMediaDataToDbUseCase,
    private val getDeviceMediaUseCase: GetDeviceMediaUseCase,
    private val generateMissingLocalHashes: GenerateMissingLocalHashes,
) : CoroutineWorker(context, workerParams) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        createNotificationChannel()

        val notification = createNotification()
        setForegroundNotificationAsync(notification)

        return try {
            Timber.tag(LOG_TAG).d("ScanDeviceMediaWorker() getDevicePhotosUseCase invoked")
            val deviceMedia = getDeviceMediaUseCase.invoke(context)
            Timber.tag(LOG_TAG).d("ScanDeviceMediaWorker() updateDeviceMediaUseCase invoked")
            updateDeviceMediaUseCase.invoke(deviceMedia)
            Timber.tag(LOG_TAG).d("ScanDeviceMediaWorker() GenerateMissingLocalHashes invoked")
            generateMissingLocalHashes(context)
            Timber.tag(LOG_TAG).d("ScanDeviceMediaWorker() Completed!!!!")
            Result.success()
        } catch (exception: Exception) {
            Timber.tag(LOG_TAG).e(exception, "Error during sync and hash: ")
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            DEVICE_MEDIA_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.sync_and_hash),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.channel_for_scan_media_worker_notifications)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification = NotificationCompat
        .Builder(
            applicationContext,
            DEVICE_MEDIA_NOTIFICATION_CHANNEL_ID
        ).setContentTitle(context.getString(R.string.scan_device_media_files))
        .setContentText(context.getString(R.string.performing_scan_device_media_operation))
        .setSmallIcon(android.R.drawable.ic_popup_sync)
        .setOngoing(true)
        .build()

    companion object {
        private const val DEVICE_MEDIA_NOTIFICATION_CHANNEL_ID = "sync_and_hash_channel"
        private const val LOG_TAG = "ScanDeviceMediaWorker"
    }
}
