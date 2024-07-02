package io.photopixels.workers.workers

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.domain.model.WorkerInfo
import io.photopixels.domain.model.WorkerStatus
import io.photopixels.domain.workers.WorkerStarter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import java.util.UUID
import javax.inject.Inject

class WorkerStarterImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerStarter {
    private var uniquePhotosWorkId: UUID? = null
    private var uniqueDevicesWorkId: UUID? = null
    private var uniqueGooglePhotosWorkId: UUID? = null

    override fun startDevicePhotosWorker() {
        getDevicePhotosWorkerRequest()
            .also {
                WorkManager.getInstance(context).enqueue(it)
            }
    }

    override fun startUploadPhotosWorker() {
        getUploadPhotosWorkerRequest()
            .also {
                WorkManager.getInstance(context).enqueue(it)
            }
    }

    override fun startDeviceAndUploadWorkers() {
        val devicePhotosWorkRequest = getDevicePhotosWorkerRequest()
        val uploadPhotosWorkRequest = getUploadPhotosWorkerRequest()
        uniqueDevicesWorkId = devicePhotosWorkRequest.id
        uniquePhotosWorkId = uploadPhotosWorkRequest.id

        WorkManager
            .getInstance(context)
            .beginWith(devicePhotosWorkRequest)
            .then(uploadPhotosWorkRequest)
            .enqueue()
    }

    override fun getUploadPhotosWorkerListener(): Flow<WorkerInfo> = WorkManager
        .getInstance(context)
        .getWorkInfoByIdFlow(uniquePhotosWorkId!!) // TODO this may be null, handle it better
        .transform { workInfo ->
            if (workInfo.state.isFinished) {
                val workerResultData = workInfo.outputData.keyValueMap
                emit(
                    WorkerInfo(
                        workerTag = workInfo.tags.first(),
                        WorkerStatus.FINISHED,
                        resultData = workerResultData
                    )
                )
            }
        }

    override fun startGooglePhotosWorker() {
        uniqueGooglePhotosWorkId = getGooglePhotosWorkerRequest()
            .also {
                WorkManager.getInstance(context).enqueue(it)
            }.id
    }

    override fun stopGooglePhotosWorker() {
        uniqueGooglePhotosWorkId?.let {
            val workerInfo = WorkManager.getInstance(context).getWorkInfoById(it)

            if (!workerInfo.get().state.isFinished) {
                WorkManager.getInstance(context).cancelAllWorkByTag(WorkerStarter.GOOGLE_PHOTOS_WORKER_TAG)
            }
        }
    }

    private fun getDevicePhotosWorkerRequest(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<DevicePhotosWorker>()
        .addTag(WorkerStarter.DEVICE_PHOTOS_WORKER_TAG)
        .build()

    private fun getUploadPhotosWorkerRequest(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<UploadPhotosWorker>()
        .addTag(WorkerStarter.UPLOAD_PHOTOS_WORKER_TAG)
        .build()

    private fun getGooglePhotosWorkerRequest(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<GooglePhotosWorker>()
        .addTag(WorkerStarter.GOOGLE_PHOTOS_WORKER_TAG)
        .build()
}
