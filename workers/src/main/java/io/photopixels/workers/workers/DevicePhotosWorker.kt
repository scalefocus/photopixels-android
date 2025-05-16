package io.photopixels.workers.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.photopixels.domain.usecases.GenerateMissingLocalHashes
import io.photopixels.domain.usecases.GetDevicePhotosUseCase
import io.photopixels.domain.usecases.UpdatePhotosDataToDbUseCase
import io.photopixels.workers.R
import timber.log.Timber

/**
 * Android worker for reading device local photos and store the information about photos in App's Database
 * Each photo has a property androidCloudId which is generated locally via fast hash algorithm
 */
@HiltWorker
class DevicePhotosWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updatePhotosUseCase: UpdatePhotosDataToDbUseCase,
    private val getDevicePhotosUseCase: GetDevicePhotosUseCase,
    private val generateMissingLocalHashes: GenerateMissingLocalHashes,
) : CoroutineWorker(context, workerParams) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val DEVICE_PHOTOS_NOTIFICATION_CHANNEL_ID = "sync_and_hash_channel"
        private const val LOG_TAG = "DevicePhotosWorker"
    }

    override suspend fun doWork(): Result {
        createNotificationChannel()

        val notification = createNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForegroundAsync(ForegroundInfo(0, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC))
        } else {
            setForegroundAsync(ForegroundInfo(0, notification))
        }

        return try {
            Timber.tag(LOG_TAG).d("DevicePhotosWorker() getDevicePhotosUseCase invoked")
            val photos = getDevicePhotosUseCase.invoke(context)
            Timber.tag(LOG_TAG).d("DevicePhotosWorker() updatePhotosUseCase invoked")
            updatePhotosUseCase.invoke(photos)
            Timber.tag(LOG_TAG).d("DevicePhotosWorker() GenerateMissingLocalHashes invoked")
            generateMissingLocalHashes(context)
            Timber.tag(LOG_TAG).d("DevicePhotosWorker() Completed!!!!")
            Result.success()
        } catch (exception: Exception) {
            Timber.tag(LOG_TAG).e(exception, "Error during sync and hash: ")
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            DEVICE_PHOTOS_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.sync_and_hash),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.channel_for_scan_photos_worker_notifications)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification = NotificationCompat
        .Builder(
            applicationContext,
            DEVICE_PHOTOS_NOTIFICATION_CHANNEL_ID
        ).setContentTitle(context.getString(R.string.scan_device_photos))
        .setContentText(context.getString(R.string.performing_scan_device_photos_operation))
        .setSmallIcon(android.R.drawable.ic_popup_sync)
        .setOngoing(true)
        .build()
}
