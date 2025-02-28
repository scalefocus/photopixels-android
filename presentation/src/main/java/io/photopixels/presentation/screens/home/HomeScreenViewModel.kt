package io.photopixels.presentation.screens.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.WorkerInfo
import io.photopixels.domain.model.WorkerStatus
import io.photopixels.domain.usecases.GetThumbnailsFromDbUseCase
import io.photopixels.domain.usecases.GetThumbnailsGroupedByMonthUseCase
import io.photopixels.domain.usecases.GetUserSettingsUseCase
import io.photopixels.domain.usecases.SaveGoogleAuthTokenUseCase
import io.photopixels.domain.usecases.SavePhotosIdsInMemoryUseCase
import io.photopixels.domain.usecases.SyncServerThumbnails
import io.photopixels.domain.workers.WorkerStarter
import io.photopixels.presentation.R
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.login.GoogleAuthorization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList", "TooManyFunctions")
class HomeScreenViewModel @Inject constructor(
    private val workerStarter: WorkerStarter,
    private val syncServerThumbnails: SyncServerThumbnails,
    private val savePhotosIdsInMemoryUseCase: SavePhotosIdsInMemoryUseCase,
    private val getThumbnailsGroupedByMonthUseCase: GetThumbnailsGroupedByMonthUseCase,
    private val getThumbnailsFromDbUseCase: GetThumbnailsFromDbUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val googleAuthorization: GoogleAuthorization,
    private val saveGoogleAuthTokenUseCase: SaveGoogleAuthTokenUseCase
) : BaseViewModel<HomeScreenState, HomeScreenActions, HomeScreenEvents>(HomeScreenState()) {

    // Used to prevent multiple starting at once of getServer thumbnails function
    private val getServerThumbnailsProgressState = MutableStateFlow(NOT_STARTED)

    init {
        viewModelScope.launch {
            getThumbnailsGroupedByMonthUseCase().collect { photoThumbnails ->
                updateState { copy(photoThumbnails = photoThumbnails) }
            }
        }

        loadStartupData()
    }

    override suspend fun handleActions(action: HomeScreenActions) {
        when (action) {
            is HomeScreenActions.OnPermissionResult -> {
                handlePermissions(action.permissionsMap)
            }

            HomeScreenActions.CloseErrorDialog -> {
                updateState { copy(errorMsgId = null) }
            }

            HomeScreenActions.OnSyncButtonClick -> {
                submitEvent(HomeScreenEvents.RequestStoragePermissionsEvent)
            }

            HomeScreenActions.StartSyncWorkers -> startWorkersAndListeners()

            is HomeScreenActions.OnThumbnailClick -> {
                savePhotosIdsInMemoryUseCase.invoke(state.value.photoThumbnails.flatMap { it.value.map { it.id } })
                submitEvent(HomeScreenEvents.NavigateToPreviewPhotosScreen(action.serverItemId))
            }

            HomeScreenActions.LoadStartupData -> {
                loadStartupData()
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun handlePermissions(permissionsMap: Map<String, Boolean>) {
        var isGranted = true

        if (permissionsMap.size == 1) {
            isGranted = permissionsMap.values.first()
        } else {
            val readFullMediaGranted = permissionsMap[Manifest.permission.READ_MEDIA_IMAGES]
            val readPartialMediaGranted = permissionsMap[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED]

            if (!readFullMediaGranted!! && !readPartialMediaGranted!!) {
                isGranted = false
            }
        }

        if (!isGranted) {
            updateState { copy(errorMsgId = R.string.error_permission_denied) }
        } else {
            startWorkersAndListeners()
        }
    }

    private fun getServerRevisionAndThumbnails(isUploadComplete: Boolean = false) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val syncResponse = syncServerThumbnails(isUploadComplete = isUploadComplete)
            if (syncResponse is Response.Failure) {
                // TODO handle with some error message later
                Timber.tag(TAG).e("Unable to Sync thumbnails from the server")
            }

            updateState { copy(isLoading = false) }
            getServerThumbnailsProgressState.emit(NOT_STARTED)
        }
    }

    private fun startWorkersAndListeners() {
        updateState { copy(isSyncStarted = true) }
        workerStarter.startDeviceAndUploadWorkers()
        initUploadPhotosWorkerListener()
    }

    private fun initUploadPhotosWorkerListener() {
        viewModelScope.launch {
            workerStarter.getUploadPhotosWorkerListener().collect {
                if (it.workerStatus == WorkerStatus.FINISHED) {
                    updateState { copy(isSyncStarted = false) }
                    // Refresh thumbnails only if photos was uploaded
                    if (it.resultData?.get(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY) as Int > 0) {
                        refreshThumbnails()
                    }
                }
            }
        }
    }

    private fun initGooglePhotosWorkerListener() {
        viewModelScope.launch {
            workerStarter.getGooglePhotosWorkerListener().collect { workerInfo ->
                if (workerInfo?.workerStatus == WorkerStatus.FAILED) {
                    val error = workerInfo.resultData?.get(WorkerInfo.WORKER_ERROR_RESULT_KEY) as String

                    if (error == PhotoPixelError.ExpiredGoogleAuthTokenError.toString()) {
                        handleGoogleError(PhotoPixelError.ExpiredGoogleAuthTokenError)
                    } else {
                        handleGoogleError(PhotoPixelError.GenericGoogleError)
                    }
                } else {
                    if (workerInfo?.resultData?.containsKey(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY) == true &&
                        workerInfo.resultData?.get(WorkerInfo.UPLOAD_PHOTOS_WORKER_RESULT_KEY) as Int > 0
                    ) {
                        // Refresh Home screen thumbnails
                        refreshThumbnails()
                    }
                }
            }
        }
    }

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

    private fun syncLocalPhotos() {
        viewModelScope.launch {
            if (getThumbnailsFromDbUseCase.getThumbnailsCount() > 0) {
                // Start auto-sync photos flow if device has at least one PP thumbnail photo
                submitEvent(HomeScreenEvents.RequestStoragePermissionsEvent)
            }
        }
    }

    private fun syncGooglePhotos() {
        viewModelScope.launch {
            val isGooglePhotosSyncEnabled = getUserSettingsUseCase.invoke()?.syncWithGoogle

            if (isGooglePhotosSyncEnabled == true) {
                initGooglePhotosWorkerListener()
                workerStarter.startGooglePhotosWorker()
            }
        }
    }

    private suspend fun handleGoogleError(exception: PhotoPixelError) {
        when (exception) {
            is PhotoPixelError.ExpiredGoogleAuthTokenError -> {
                // Google Token expired Exception
                performGoogleRefreshRequest()
            }

            else -> {
                // TODO: Generic Google Error(Hidden from the user for now)
                Timber.tag(TAG).e("Unable to Sync with Google")
            }
        }
    }

    private suspend fun performGoogleRefreshRequest() {
        googleAuthorization.performRefreshTokenRequest().collect { isSuccessful ->
            isSuccessful?.let {
                if (it) {
                    googleAuthorization.getGoogleAuthTokenFlow().collect { googleAuthToken ->
                        googleAuthToken?.let {
                            Timber.tag(TAG).d("Start Google Photos sync after token refresh process")

                            // Start again Google Photos worker, when new authToken is received
                            saveGoogleAuthTokenUseCase.invoke(googleAuthToken)
                            workerStarter.startGooglePhotosWorker()
                        }
                    }
                } else {
                    updateState { copy(errorMsgId = R.string.error_google_token_expire) }
                }
            }
        }
    }

    private fun loadStartupData() {
        syncLocalPhotos()
        syncGooglePhotos()
        // TODO Thumbnails can be stored locally in DB, and if latest revision from server is equal to local revision
        // equal -> load thumbnails from device
        // not equal -> load thumbnails from server and then store it in device

        getServerThumbnailsProgressState.value = STARTED
        getServerRevisionAndThumbnails()
    }

    companion object {
        private const val STARTED = true
        private const val NOT_STARTED = false
        private const val TAG = "HOME_SCREEN_VIEWMODEL"
    }
}
