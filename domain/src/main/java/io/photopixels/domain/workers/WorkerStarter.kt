package io.photopixels.domain.workers

import io.photopixels.domain.model.WorkerInfo
import kotlinx.coroutines.flow.Flow

interface WorkerStarter {
    companion object {
        const val DEVICE_PHOTOS_WORKER_TAG = "device_photos_worker"
        const val UPLOAD_PHOTOS_WORKER_TAG = "upload_photos_worker"
    }

    fun startDevicePhotosWorker()

    fun startUploadPhotosWorker()

    fun startDeviceAndUploadWorkers()

    fun getUploadPhotosWorkerListener(): Flow<WorkerInfo>
}
