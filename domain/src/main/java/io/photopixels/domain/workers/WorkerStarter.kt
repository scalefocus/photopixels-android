package io.photopixels.domain.workers

import io.photopixels.domain.model.WorkerInfo
import kotlinx.coroutines.flow.Flow

interface WorkerStarter {
    companion object {
        const val SCAN_DEVICE_MEDIA_WORKER_TAG = "device_media_worker"
        const val UPLOAD_DEVICE_MEDIA_WORKER_TAG = "upload_media_worker"
        const val GOOGLE_PHOTOS_WORKER_TAG = "google_photos_worker"
    }

    fun startDevicePhotosWorker()

    fun startUploadPhotosWorker()

    fun startDeviceAndUploadWorkers()

    fun schedulePeriodicSyncWorker()

    fun getUploadPhotosWorkerListener(): Flow<WorkerInfo>

    fun stopDevicePhotosWorkers()

    fun startGooglePhotosWorker(sessionId: String, pollingInterval: String)

    fun stopGooglePhotosWorker()

    fun getGooglePhotosWorkerListener(): Flow<WorkerInfo?>
}
