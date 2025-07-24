package io.photopixels.workers.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.photopixels.domain.usecases.GetUserSettingsUseCase
import io.photopixels.domain.workers.WorkerStarter
import timber.log.Timber

private const val LOG_TAG = "PeriodicSyncWorker"

/**
 * A Wrapper worker to execute Device and Upload workers periodically.
 */
@HiltWorker
class PeriodicSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workerStarter: WorkerStarter,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.tag(LOG_TAG).d("doWork started")

        val requireWifi = getUserSettingsUseCase.invoke().requireWifi
        // enqueue Device and Upload photos workers
        workerStarter.startScanAndUploadWorkers(requireWifi)

        Timber.tag(LOG_TAG).d("doWork finished")

        return Result.success()
    }
}
