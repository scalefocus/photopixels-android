package io.photopixels.presentation.screens.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.WorkerInfo
import io.photopixels.domain.model.WorkerStatus
import io.photopixels.domain.usecases.GetServerRevisionUseCase
import io.photopixels.domain.usecases.GetServerThumbnailsUseCase
import io.photopixels.domain.usecases.GetThumbnailsFromDbUseCase
import io.photopixels.domain.usecases.GetUserSettingsUseCase
import io.photopixels.domain.usecases.SaveGoogleAuthTokenUseCase
import io.photopixels.domain.usecases.SavePhotosIdsInMemoryUseCase
import io.photopixels.domain.usecases.SaveThumbnailsToDbUseCase
import io.photopixels.domain.usecases.googlephotos.GetGooglePhotosUseCase
import io.photopixels.domain.workers.WorkerStarter
import io.photopixels.presentation.R
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.login.GoogleAuthorization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList")
class HomeScreenViewModel @Inject constructor(
    private val workerStarter: WorkerStarter,
    private val getServerRevisionUseCase: GetServerRevisionUseCase,
    private val getServerThumbnailsUseCase: GetServerThumbnailsUseCase,
    private val savePhotosIdsInMemoryUseCase: SavePhotosIdsInMemoryUseCase,
    private val getThumbnailsFromDbUseCase: GetThumbnailsFromDbUseCase,
    private val saveThumbnailsToDbUseCase: SaveThumbnailsToDbUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val getGooglePhotosUseCase: GetGooglePhotosUseCase,
    private val googleAuthorization: GoogleAuthorization,
    private val saveGoogleAuthTokenUseCase: SaveGoogleAuthTokenUseCase
) : BaseViewModel<HomeScreenState, HomeScreenActions, HomeScreenEvents>(HomeScreenState()) {

    // Used to prevent multiple starting at once of getServer thumbnails function
    private val getServerThumbnailsProgressState = MutableStateFlow(NOT_STARTED)

    init {
        syncLocalPhotos()
        syncGooglePhotos()
        // TODO Thumbnails can be stored locally in DB, and if latest revision from server is equal to local revision
        // equal -> load thumbnails from device
        // not equal -> load thumbnails from server and then store it in device

        getServerThumbnailsProgressState.value = true
        getServerRevisionAndThumbnails()
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
                savePhotosIdsInMemoryUseCase.invoke(state.value.photoThumbnails.map { it.id })
                submitEvent(HomeScreenEvents.NavigateToPreviewPhotosScreen(action.serverItemId))
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

            getServerRevisionUseCase(null).collect { revisionResponse ->
                if (revisionResponse is Response.Success) {
                    val serverItemsIdsList = revisionResponse.result.added.map { it.key }
                    getServerThumbnailsChunked(serverItemsIdsList, isUploadComplete)
                } else if (revisionResponse is Response.Failure) {
                    when (revisionResponse.error) {
                        is PhotoPixelError.NoInternetConnection -> {
                            val thumbnailsFromDb = getThumbnailsFromDb()
                            updateState { copy(photoThumbnails = thumbnailsFromDb) }
                        }

                        else -> {
                            // TODO handle with some error message later
                        }
                    }
                    updateState { copy(isLoading = false) }
                }
            }

            getServerThumbnailsProgressState.emit(false)
        }
    }

    private suspend fun getServerThumbnailsChunked(serverItemsIdsList: List<String>, isUploadComplete: Boolean) {
        val chunkSize = MAX_OBJECTS_TO_REQUEST
        val serverThumbnails = mutableListOf<PhotoUiData>()
        val serverItemsIdsMap = mutableMapOf<Int, List<String>>()

        serverItemsIdsList.chunked(chunkSize) { chunkedServerItems ->
            serverItemsIdsMap.put(chunkedServerItems.size, chunkedServerItems.toList())
        }

        serverItemsIdsMap.forEach { (_, value) ->
            val result = getServerThumbnails(chunkedServerItemsIds = value, isUploadComplete)
            serverThumbnails.addAll(result)
        }

        val itemsToShow = serverThumbnails.reversed().distinctBy { it.hash }
        saveThumbnailsToDbUseCase.invoke(itemsToShow)
        updateState { HomeScreenState(isLoading = false, photoThumbnails = itemsToShow) }
    }

    private suspend fun getServerThumbnails(
        chunkedServerItemsIds: List<String>,
        isUploadComplete: Boolean
    ): List<PhotoUiData> {
        val serverThumbnailsResponse = getServerThumbnailsUseCase(chunkedServerItemsIds)
        if (serverThumbnailsResponse is Response.Success) {
            return if (isUploadComplete) {
                // Determine newly added items by comparing hashes
                val existingPhotoHashes = state.value.photoThumbnails
                    .map { it.hash }
                    .toSet()
                val newItems = serverThumbnailsResponse.result.filter {
                    !existingPhotoHashes.contains(it.hash)
                }

                newItems.forEach { it.isNewlyUploaded = true }
                val totalItems = state.value.photoThumbnails + newItems

                totalItems
            } else {
                serverThumbnailsResponse.result
            }
        }

        return emptyList()
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
                }
            }
        }
    }

    // TODO Implement logic for load Thumbnails from db, when app's revision and BE revision are equal
    // NOTE: Currently we don't have latest backend revision, until we execute /revision service
    // Suggestion made: we can add latest backend revision to /status or /login endpoints
    private suspend fun getThumbnailsFromDb(): List<PhotoUiData> = getThumbnailsFromDbUseCase.invoke()

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
                Timber.tag("TAG").e("HomeScreenViewModel() Start Google Photos Sync")
                val exception = getGooglePhotosUseCase.invoke()

                if (exception != null) {
                    handleGoogleError(exception)
                } else {
                    workerStarter.startGooglePhotosWorker()
                }
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
                // Generic Google exception
                // TODO: Show error to the user, that Google session is expired or similar, and needs to
                // re-login
            }
        }
    }

    private suspend fun performGoogleRefreshRequest() {
        googleAuthorization.performRefreshTokenRequest().collect { isSuccessful ->
            if (isSuccessful == true) {
                googleAuthorization.getGoogleAuthTokenFlow().collect { googleAuthToken ->
                    googleAuthToken?.let {
                        Timber.tag(TAG).d("Start Google Photos sync after token refresh process")

                        // Start again Google Photos worker, when new authToken is received
                        saveGoogleAuthTokenUseCase.invoke(googleAuthToken)
                        getGooglePhotosUseCase.invoke()
                        workerStarter.startGooglePhotosWorker()
                    }
                }
            } else {
                // TODO: Show error to the user, that Google session is expired or similar, and needs to
                // re-login
            }
        }
    }

    companion object {
        private const val MAX_OBJECTS_TO_REQUEST = 90
        private const val STARTED = true
        private const val NOT_STARTED = false
        private const val TAG = "HOME_SCREEN_VIEWMODEL"
    }
}
