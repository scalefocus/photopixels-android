package io.photopixels.presentation.screens.photos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.Response
import io.photopixels.domain.usecases.DeletePhotoUseCase
import io.photopixels.domain.usecases.GetAuthHeaderUseCase
import io.photopixels.domain.usecases.GetPhotosIdsFromMemory
import io.photopixels.domain.usecases.GetServerInfoUseCase
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.base.Constants.THUMBNAIL_ID_ARGUMENT_NAME
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosPreviewViewModel @Inject constructor(
    private val getPhotosIdsInMemoryUseCase: GetPhotosIdsFromMemory,
    private val getServerInfoUseCase: GetServerInfoUseCase,
    private val getAuthHeaderUseCase: GetAuthHeaderUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val savedState: SavedStateHandle
) : BaseViewModel<PhotosPreviewScreenState, PhotosPreviewActions, PhotosPreviewEvents>(PhotosPreviewScreenState()) {
    private var photosIds = mutableListOf<String>()

    init {
        viewModelScope.launch {
            savedState.get<Array<String>>(THUMBNAIL_ID_ARGUMENT_NAME)?.let { preparePhotoUrls(it[0]) }
        }
    }

    override suspend fun handleActions(action: PhotosPreviewActions) {
        when (action) {
            PhotosPreviewActions.OnDeleteIconClicked -> {
                updateState { copy(isDeleteDialogVisible = true) }
            }
            is PhotosPreviewActions.OnDeletePhotoClick -> {
                updateState { copy(isDeleteDialogVisible = false) }
                deletePhoto(action.photoIndex)
            }

            PhotosPreviewActions.OnDeleteDialogCancelClick -> {
                updateState { copy(isDeleteDialogVisible = false) }
            }
        }
    }

    private fun preparePhotoUrls(clickedThumbnailServerId: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            val serverAddress = getServerInfoUseCase.getServerAddress()
            serverAddress?.let {
                val authHeader = getAuthHeaderUseCase.invoke()
                authHeader?.let {
                    photosIds = getPhotosIdsInMemoryUseCase.invoke().toMutableList()
                    val photosGlideUrls = photosIds.map { buildGlideUrl(it, serverAddress.toString(), authHeader) }
                    val photoToLoadFirstIndex = photosIds.indexOf(clickedThumbnailServerId)
                    updateState {
                        copy(
                            photosGlideUrls = photosGlideUrls,
                            photoToLoadFirstIndex = photoToLoadFirstIndex,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun deletePhoto(imageIndex: Int) {
        updateState { copy(isLoading = true) }
        val photoServerId = photosIds[imageIndex]
        val result = deletePhotoUseCase.invoke(photoServerId)

        if (result is Response.Success) {
            submitEvent(PhotosPreviewEvents.OnPhotoDeletedSuccessfully)

            // Update UI after photo deletion
            val newImages: List<GlideUrl> = state.value.photosGlideUrls.toMutableList().apply {
                removeAt(imageIndex)
            }
            photosIds.removeAt(imageIndex)
            updateState { copy(photosGlideUrls = newImages) }
        } else {
            submitEvent(PhotosPreviewEvents.OnPhotoDeleteFail)
        }
        updateState { copy(isLoading = false) }
    }

    private fun buildGlideUrl(photoId: String, serverAddress: String, authHeader: String): GlideUrl = GlideUrl(
        "$serverAddress/api/object/$photoId",
        LazyHeaders
            .Builder()
            .addHeader(
                "Authorization",
                "Bearer $authHeader"
            ).build()
    )
}
