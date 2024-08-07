package io.photopixels.workers.workers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.WorkerInfo
import io.photopixels.domain.usecases.DownloadPhotoUseCase
import io.photopixels.domain.usecases.GetPhotosForUploadUseCase
import io.photopixels.domain.usecases.UpdatePhotoInDbUseCase
import io.photopixels.domain.usecases.UploadPhotoUseCase
import io.photopixels.domain.usecases.googlephotos.GetGooglePhotosUseCase
import io.photopixels.domain.utils.Hasher
import io.photopixels.presentation.notifications.NotificationsHelper
import io.photopixels.workers.R
import timber.log.Timber

@HiltWorker
class GooglePhotosWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPhotosForUploadUseCase: GetPhotosForUploadUseCase,
    private val downloadPhotoUseCase: DownloadPhotoUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val updatePhotoInDbUseCase: UpdatePhotoInDbUseCase,
    private val notificationsHelper: NotificationsHelper,
    private val getGooglePhotosUseCase: GetGooglePhotosUseCase,
) : CoroutineWorker(appContext, workerParams) {
    private var uploadedPhotosCounter = 0

    @SuppressLint("MissingPermission")
    @SuppressWarnings("ReturnCount")
    override suspend fun doWork(): Result {
        Timber.tag(LOG_TAG).e("GOOGLE_PHOTOS WORKER STARTED!!!")
        var outputData: Data? = null

        createChannel()
        createNotification(
            notificationTitleId = R.string.upload_google_photos,
            notificationContentId = R.string.upload_google_photos_description,
            isOngoing = true
        )

        Timber.tag(LOG_TAG).d("Download Google photos")
        getGooglePhotosUseCase.invoke()?.let { error ->
            val output = Data
                .Builder()
                .putString(WorkerInfo.WORKER_ERROR_RESULT_KEY, error.toString())
                .build()
            return Result.failure(output)
        }

        Timber.tag(LOG_TAG).e("Google photos download completed")
        val photosForUpload = getPhotosForUploadUseCase.getGooglePhotosFromDB()
        photosForUpload.forEach { googlePhoto ->

            val photoResult = downloadPhotoUseCase.downloadGooglePhoto(googlePhoto.baseUrl)
            if (photoResult is Response.Success) {
                val photoBytes = photoResult.result

                Timber.tag(LOG_TAG).d("googlePhoto:${googlePhoto.fileName} Downloaded!!!")
                uploadGooglePhoto(googlePhoto, photoBytes)
            } else if (photoResult is Response.Failure) {
                // TODO: handle download error
            }
        }

        if (uploadedPhotosCounter > 0) { // Show notification if at least 1 photo has been uploaded
            createNotification(
                notificationTitleId = R.string.upload_google_photos,
                notificationContentId = R.string.upload_google_photos_completed,
                notificationIconId = android.R.drawable.stat_sys_upload_done,
                isOngoing = false,
                autoCancel = true,
                isForeground = false
            )

            outputData =
                Data.Builder().putInt(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY, uploadedPhotosCounter).build()
            return Result.success(outputData)
        } else {
            Timber.tag(LOG_TAG).d("GOOGLE_PHOTOS are UP-TO date, nothing for upload to PhotoPixels cloud")
        }

        outputData =
            Data.Builder().putInt(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY, 0).build()
        return Result.success(outputData)
    }

    private suspend fun uploadGooglePhoto(googlePhotoData: GooglePhoto, googlePhotoBytes: ByteArray) {
        Timber.tag(LOG_TAG).d("Trying to upload googlePhoto:${googlePhotoData.fileName} to PhotoPixel")

        // Uploading Photo
        val photoUploadResult = uploadPhotoUseCase.invoke(
            fileBytes = googlePhotoBytes,
            androidCloudId = googlePhotoData.androidCloudId ?: "",
            fileName = googlePhotoData.fileName,
            mimeType = googlePhotoData.mimeType,
            objectHash = Hasher.sha1HashBase64(googlePhotoBytes)
        )

        updateGooglePhotoInDB(photoUploadResult, googlePhotoData)
    }

    private suspend fun updateGooglePhotoInDB(
        photoUploadResult: Response<PhotoUploadData>,
        googlePhotoData: GooglePhoto
    ) {
        when (photoUploadResult) {
            is Response.Success -> {
                // Update photoData in DB
                val updatedPhotoData =
                    googlePhotoData.copy(
                        serverItemHashId = photoUploadResult.result.id,
                        isAlreadyUploaded = true
                    )
                updatePhotoInDbUseCase.updatedGooglePhotoData(updatedPhotoData)
                uploadedPhotosCounter++
            }

            is Response.Failure -> {
                Timber.tag(LOG_TAG).e("Error uploading photo to PhotoPixels, ${googlePhotoData.fileName}")
                if (photoUploadResult.error is PhotoPixelError.DuplicatePhotoError) {
                    // Photo is already uploaded
                    val updatedPhotoData = googlePhotoData.copy(isAlreadyUploaded = true)
                    updatePhotoInDbUseCase.updatedGooglePhotoData(updatedPhotoData)
                }
            }
        }
    }

    private fun createChannel() {
        notificationsHelper.createNotificationChannel(
            GOOGLE_PHOTOS_NOTIFICATION_CHANNEL_ID,
            appContext.getString(R.string.upload_google_photos),
            appContext.getString(R.string.channel_for_upload_photos_worker_notifications)
        )
    }

    private fun createNotification(
        @StringRes notificationTitleId: Int,
        @StringRes notificationContentId: Int,
        isOngoing: Boolean,
        autoCancel: Boolean = false,
        isForeground: Boolean = true,
        @DrawableRes notificationIconId: Int = android.R.drawable.ic_popup_sync
    ) {
        val notification = notificationsHelper.createNotification(
            channelId = GOOGLE_PHOTOS_NOTIFICATION_CHANNEL_ID,
            notificationTitle = appContext.getString(notificationTitleId),
            notificationContent = appContext.getString(notificationContentId),
            isOngoing = isOngoing,
            autoCancel = autoCancel,
            notificationIconId = notificationIconId
        )

        if (isForeground) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setForegroundAsync(
                    ForegroundInfo(
                        FOREGROUND_SERVICE_NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )
                )
            } else {
                setForegroundAsync(ForegroundInfo(FOREGROUND_SERVICE_NOTIFICATION_ID, notification))
            }
        } else {
            notificationsHelper.showNotification(NORMAL_NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val GOOGLE_PHOTOS_NOTIFICATION_CHANNEL_ID = "google_photos_channel"
        private const val LOG_TAG = "GooglePhotosWorker"
        private const val FOREGROUND_SERVICE_NOTIFICATION_ID = 2
        private const val NORMAL_NOTIFICATION_ID = 3
    }
}
