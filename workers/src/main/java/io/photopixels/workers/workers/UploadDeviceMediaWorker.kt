package io.photopixels.workers.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.exceptions.ResumableUploadException
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.WorkerInfo
import io.photopixels.domain.usecases.GetMediaFilesForUploadUseCase
import io.photopixels.domain.usecases.RemoveDeviceMediaDataFromDbUseCase
import io.photopixels.domain.usecases.SyncServerThumbnailsUseCase
import io.photopixels.domain.usecases.UpdateMediaInDbUseCase
import io.photopixels.domain.usecases.UploadMediaFileUseCase
import io.photopixels.workers.R
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.math.roundToInt

/**
 * A worker that is responsible for uploading new device media files to PhotoPixel backend
 */
@HiltWorker
class UploadDeviceMediaWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getMediaFilesForUploadUseCase: GetMediaFilesForUploadUseCase,
    private val uploadMediaFileUseCase: UploadMediaFileUseCase,
    private val updateMediaInDbUseCase: UpdateMediaInDbUseCase,
    private val removeDeviceMediaDataFromDbUseCase: RemoveDeviceMediaDataFromDbUseCase,
    private val syncServerThumbnailsUseCase: SyncServerThumbnailsUseCase,
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        Timber.tag(LOG_TAG).e("UploadDeviceMediaWorker STARTED!!!!")
        var uploadedFilesCount = 0

        createNotificationChannel()

        val notification = createNotification()

        setForegroundNotificationAsync(notification)

        val mediaFilesToUpload = getMediaFilesForUploadUseCase.invoke()
        Timber.tag(LOG_TAG).e("UploadDeviceMediaWorker() Files for Upload: $mediaFilesToUpload")
        mediaFilesToUpload.forEachIndexed { index, deviceMedia ->
            processDeviceMediaData(
                deviceMedia = deviceMedia,
                currentFileIndex = index,
                fileCount = mediaFilesToUpload.size,
                onUploadSuccess = {
                    uploadedFilesCount++
                }
            )
        }

        val output = Data
            .Builder()
            .putInt(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY, uploadedFilesCount)
            .build()
        Timber.tag(LOG_TAG).e(
            "UploadDeviceMediaWorker() COMPLETED , uploadedFilesCount: $uploadedFilesCount \n" +
                "outputData: ${output.keyValueMap}"
        )
        return Result.success(output)
    }

    private suspend fun processDeviceMediaData(
        deviceMedia: DeviceMedia,
        onUploadSuccess: () -> Unit,
        currentFileIndex: Int,
        fileCount: Int
    ) {
        try {
            // Uploading device media file
            Timber.tag(LOG_TAG).e("UploadDeviceMediaWorker() Uploading file: ${deviceMedia.hash}")
            uploadMediaFileUseCase.invoke(
                deviceMedia.contentUri.toUri(),
                deviceMedia.fileName,
                deviceMedia.hash.orEmpty()
            )
                .collect { progress ->
                    setForegroundNotificationAsync(
                        createProgressNotification(progress.roundToInt(), currentFileIndex, fileCount)
                    )
                }

            Timber.tag(LOG_TAG).e("UploadDeviceMediaWorker() Files is uploaded: ${deviceMedia.hash}")

            // files uploaded successfully
            onUploadSuccess()
            syncServerThumbnailsUseCase(isUploadComplete = true)
        } catch (exception: FileNotFoundException) {
            Timber.tag(LOG_TAG).e("File not found while uploading: $exception")
            removeDeviceMediaDataFromDbUseCase.invoke(deviceMedia.id.toInt())
        } catch (exception: ResumableUploadException) {
            if (exception.error is PhotoPixelError.DuplicatePhotoError) {
                // Files is already uploaded
                val updatedDeviceMediaData = deviceMedia.copy(isAlreadyUploaded = true)
                updateMediaInDbUseCase.invoke(updatedDeviceMediaData)
            } else {
                Timber.tag(LOG_TAG).e(exception.cause, "Server error while uploading the file")
            }
        } catch (exception: IOException) {
            Timber.tag(LOG_TAG).e(exception, "Other error while uploading the file")
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            UPLOAD_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.sync_and_hash),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.channel_for_upload_media_worker_notifications)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification = NotificationCompat
        .Builder(
            applicationContext,
            UPLOAD_NOTIFICATION_CHANNEL_ID
        ).setContentTitle(context.getString(R.string.upload_device_media_title))
        .setContentText(context.getString(R.string.upload_device_media_description))
        .setSmallIcon(android.R.drawable.ic_popup_sync)
        .setOngoing(true)
        .build()

    private fun createProgressNotification(
        uploadProgress: Int,
        currentFileIndex: Int,
        fileCount: Int,
    ): Notification = NotificationCompat.Builder(applicationContext, UPLOAD_NOTIFICATION_CHANNEL_ID)
        .setContentTitle(context.getString(R.string.upload_device_media_title))
        .setContentText(context.getString(R.string.upload_file_progress_description, currentFileIndex + 1, fileCount))
        .setSmallIcon(android.R.drawable.ic_popup_sync)
        .setProgress(MAX_PROGRESS, uploadProgress, false)
        .setOngoing(true)
        .build()

    companion object {
        private const val UPLOAD_NOTIFICATION_CHANNEL_ID = "upload_media_channel"
        private const val LOG_TAG = "UploadDeviceMediaWorker"
        private const val MAX_PROGRESS = 100
    }
}
