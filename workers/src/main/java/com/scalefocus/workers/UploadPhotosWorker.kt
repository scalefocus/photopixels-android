package com.scalefocus.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.scalefocus.domain.base.PhotoPixelError
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.PhotoData
import com.scalefocus.domain.model.WorkerInfo
import com.scalefocus.domain.usecases.GetPhotosForUploadUseCase
import com.scalefocus.domain.usecases.RemovePhotoDataFromDbUseCase
import com.scalefocus.domain.usecases.UpdatePhotoInDbUseCase
import com.scalefocus.domain.usecases.UploadPhotoUseCase
import com.scalefocus.domain.utils.Hasher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

/**
 * Upload photos worker. This worker is responsible for uploading new device photos to PhotoPixel backend
 */
@HiltWorker
class UploadPhotosWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPhotosForUploadUseCase: GetPhotosForUploadUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val updatePhotoInDbUseCase: UpdatePhotoInDbUseCase,
    private val removePhotoDataFromDbUseCase: RemovePhotoDataFromDbUseCase
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        Timber.tag(LOG_TAG).e("UploadPhotosWorker STARTED!!!!")
        var uploadedPhotosCount = 0

        createNotificationChannel()

        val notification = createNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForegroundAsync(ForegroundInfo(0, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC))
        } else {
            setForegroundAsync(ForegroundInfo(0, notification))
        }

        val photosDataList = getPhotosForUploadUseCase.invoke()
        Timber.tag(LOG_TAG).e("UploadPhotosWorker() Photos for Upload: $photosDataList")
        photosDataList.forEach { photoData ->
            processPhotoData(
                photoData = photoData,
                onUploadSuccess = {
                    uploadedPhotosCount++
                }
            )
        }

        val output = Data.Builder()
            .putInt(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY, uploadedPhotosCount)
            .build()
        Timber.tag(LOG_TAG).e(
            "UploadPhotosWorker() COMPLETED , uploadedPhotosCount: $uploadedPhotosCount \n" +
                "outputData: ${output.keyValueMap}"
        )
        return Result.success(output)
    }

    private suspend fun processPhotoData(photoData: PhotoData, onUploadSuccess: () -> Unit) {
        try {
            val fileBytes = readFileContent(context, Uri.parse(photoData.contentUri)) ?: return

            // Uploading Photo
            val photoUploadResult = uploadPhotoUseCase.invoke(
                fileBytes = fileBytes,
                androidCloudId = photoData.androidCloudId ?: "",
                fileName = photoData.fileName,
                mimeType = photoData.mimeType,
                objectHash = Hasher.sha1HashBase64(fileBytes)
            )

            when (photoUploadResult) {
                is Response.Success -> {
                    // Update photoData in DB
                    val updatedPhotoData =
                        photoData.copy(serverItemHashId = photoUploadResult.result.id, isAlreadyUploaded = true)
                    updatePhotoInDbUseCase.invoke(updatedPhotoData)
                    onUploadSuccess()
                }

                is Response.Failure -> {
                    Timber.tag(LOG_TAG).e("Error uploading photo, ${photoData.fileName}")
                    if (photoUploadResult.error is PhotoPixelError.DuplicatePhotoError) {
                        // Photo is already uploaded
                        val updatedPhotoData = photoData.copy(isAlreadyUploaded = true)
                        updatePhotoInDbUseCase.invoke(updatedPhotoData)
                    }
                }
            }
        } catch (exception: FileNotFoundException) {
            Timber.tag(LOG_TAG).e("File not found while uploading: $exception")
            removePhotoDataFromDbUseCase.invoke(photoData.id.toInt())
        }
    }

    private fun readFileContent(context: Context, uri: Uri): ByteArray? {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val buffer = ByteArrayOutputStream()
            val data = ByteArray(READ_BUFFER_SIZE)
            var nRead: Int
            while (inputStream.read(data).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.flush()
            return buffer.toByteArray()
        }
        return null
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            UPLOAD_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.sync_and_hash),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.channel_for_upload_photos_worker_notifications)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, UPLOAD_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.upload_photos))
            .setContentText(context.getString(R.string.upload_photos_description))
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val UPLOAD_NOTIFICATION_CHANNEL_ID = "upload_photos_channel"
        private const val LOG_TAG = "UploadPhotosWorker"
        private const val READ_BUFFER_SIZE = 1024
    }
}
