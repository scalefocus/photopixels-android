package io.photopixels.presentation.screens.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.WorkerInfo
import io.photopixels.domain.model.WorkerStatus
import io.photopixels.domain.usecases.GetThumbnailsGroupedByMonthUseCase
import io.photopixels.domain.usecases.ScanDevicePhotosUseCase
import io.photopixels.domain.usecases.SyncServerThumbnailsUseCase
import io.photopixels.domain.workers.WorkerStarter
import io.photopixels.presentation.R
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.permissions.StorageAccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class HomeScreenViewModel @Inject constructor(
    private val workerStarter: WorkerStarter,
    private val syncServerThumbnailsUseCase: SyncServerThumbnailsUseCase,
    private val getThumbnailsGroupedByMonthUseCase: GetThumbnailsGroupedByMonthUseCase,
    private val scanDevicePhotosUseCase: ScanDevicePhotosUseCase,
    private val mediaObserver: MediaObserverHelper,
) : BaseViewModel<HomeScreenState, HomeScreenActions, HomeScreenEvents>(HomeScreenState()) {

    // Used to prevent multiple starting at once of getServer thumbnails function
    private val getServerThumbnailsProgressState = MutableStateFlow(NOT_STARTED)

    private var storageAccess = StorageAccess.Denied
        set(value) {
            field = value
            if (value != StorageAccess.Denied) {
                registerContentObserver()
            }
        }

    private var scanDevicePhotosJob: Job? = null
    private var hasLocalMediaChanges = false

    init {
        viewModelScope.launch {
            getThumbnailsGroupedByMonthUseCase().collect { photoThumbnails ->
                updateState { copy(photoThumbnails = photoThumbnails) }
            }
        }
    }

    private fun scanDeviceMedia() {
        if (scanDevicePhotosJob == null) {
            // start scanning device media
            scanDevicePhotosJob = viewModelScope.launch {
                scanDevicePhotosUseCase()
                scanDevicePhotosJob = null

                if (hasLocalMediaChanges) {
                    // there are new changes in device media, sync them
                    hasLocalMediaChanges = false
                    scanDeviceMedia()
                } else {
                    // device media is synced, start upload worker
                    workerStarter.startUploadMediaWorker()
                }
            }
        } else {
            // device media job is already running, wait for it to finish
            hasLocalMediaChanges = true
        }
    }

    override fun onCleared() {
        mediaObserver.unregisterObserver()
    }

    override suspend fun handleActions(action: HomeScreenActions) {
        when (action) {
            is HomeScreenActions.UpdateStorageAccess -> storageAccess = action.storageAccess

            is HomeScreenActions.OnPermissionResult -> {
                storageAccess = action.storageAccess
                handlePermissions(action.storageAccess)
            }

            HomeScreenActions.CloseErrorDialog -> {
                updateState { copy(errorMsgId = null) }
            }

            HomeScreenActions.OnSyncButtonClick -> {
                syncLocalPhotos()
            }

            HomeScreenActions.StartSyncWorkers -> startWorkersAndListeners()

            is HomeScreenActions.OnThumbnailClick -> {
                submitEvent(HomeScreenEvents.NavigateToPreviewPhotosScreen(action.thumbnailId))
            }

            HomeScreenActions.LoadStartupData -> {
                loadStartupData()
            }
        }
    }

    private fun registerContentObserver() {
        if (!mediaObserver.isRegistered) {
            mediaObserver.registerObserver {
                scanDeviceMedia()
            }

            loadStartupData(isUserAction = false)
        }
    }

    private fun handlePermissions(storageAccess: StorageAccess) {
        if (storageAccess == StorageAccess.Denied) {
            updateState { copy(errorMsgId = R.string.error_permission_denied) }
        } else {
            workerStarter.schedulePeriodicSyncWorker()
            startWorkersAndListeners()
        }
    }

    private suspend fun getServerRevisionAndThumbnails(isUploadComplete: Boolean = false) {
        updateState { copy(isLoading = true) }
        val syncResponse = syncServerThumbnailsUseCase(isUploadComplete = isUploadComplete)
        if (syncResponse is Response.Failure) {
            // TODO handle with some error message later
            Timber.tag(TAG).e("Unable to Sync thumbnails from the server")
        }

        updateState { copy(isLoading = false) }
        getServerThumbnailsProgressState.emit(NOT_STARTED)
    }

    private fun startWorkersAndListeners() {
        updateState { copy(isSyncStarted = true) }
        workerStarter.startScanAndUploadWorkers()
        initUploadPhotosWorkerListener()
    }

    private fun initUploadPhotosWorkerListener() {
        viewModelScope.launch {
            workerStarter.getUploadMediaWorkerListener().collect { workerInfo ->
                if (workerInfo.workerStatus == WorkerStatus.FINISHED) {
                    updateState { copy(isSyncStarted = false) }

                    if (workerInfo.uploadedPhotosCount > 0) {
                        refreshThumbnails()
                    }
                }
            }
        }
    }

    private val WorkerInfo.uploadedPhotosCount: Int
        get() = resultData?.get(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY)
            ?.let { uploadPhotoResult -> uploadPhotoResult as? Int } ?: 0

    private suspend fun refreshThumbnails() {
        // Wait if function is already started
        if (getServerThumbnailsProgressState.value == STARTED) {
            getServerThumbnailsProgressState.collect { state ->
                if (state == NOT_STARTED) {
                    getServerRevisionAndThumbnails(isUploadComplete = true)
                }
            }
        } else {
            getServerRevisionAndThumbnails(isUploadComplete = true)
        }
    }

    private fun syncLocalPhotos(isUserAction: Boolean = true) {
        if (storageAccess == StorageAccess.Denied) {
            if (isUserAction) submitEvent(HomeScreenEvents.RequestStoragePermissionsEvent)
        } else {
            startWorkersAndListeners()
        }
    }

    private fun loadStartupData(isUserAction: Boolean = true) {
        getServerThumbnailsProgressState.value = STARTED
        viewModelScope.launch {
            getServerRevisionAndThumbnails()

            syncLocalPhotos(isUserAction)
        }
    }

    companion object {
        private const val STARTED = true
        private const val NOT_STARTED = false
        private const val TAG = "HOME_SCREEN_VIEWMODEL"
    }
}
