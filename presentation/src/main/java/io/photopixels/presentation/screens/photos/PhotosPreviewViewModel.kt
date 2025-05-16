package io.photopixels.presentation.screens.photos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.usecases.DeletePhotoUseCase
import io.photopixels.domain.usecases.GetServerInfoUseCase
import io.photopixels.domain.usecases.GetThumbnailsFromDbUseCase
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.base.routes.HomeScreens
import io.photopixels.presentation.screens.photos.PhotosPreviewScreenState.PhotoPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosPreviewViewModel @Inject constructor(
    private val getThumbnailsUseCase: GetThumbnailsFromDbUseCase,
    private val getServerInfoUseCase: GetServerInfoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    savedState: SavedStateHandle
) : BaseViewModel<PhotosPreviewScreenState, PhotosPreviewActions, PhotosPreviewEvents>(PhotosPreviewScreenState()) {

    init {
        val route = savedState.toRoute<HomeScreens.PhotosPreview>()
        preparePhotoUrls(route.thumbnailServerItemId)
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

    private fun preparePhotoUrls(clickedThumbnailId: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            val serverAddress = getServerInfoUseCase.getServerAddress()?.toString()
            serverAddress?.let {
                val thumbnails = getThumbnailsUseCase.invoke().first()
                val photos = thumbnails.map {
                    when (it) {
                        is Thumbnail.LocalThumbnail -> PhotoPreview.Local(it.id, it.contentUri)
                        is Thumbnail.RemoteThumbnail -> {
                            PhotoPreview.Remote(
                                id = it.id,
                                photoUrl = formatRemoteUrl(it.id, serverAddress)
                            )
                        }
                    }
                }
                val photoToLoadFirstIndex = thumbnails.indexOfFirst { it.id == clickedThumbnailId }
                updateState {
                    copy(
                        photos = photos,
                        photoToLoadFirstIndex = photoToLoadFirstIndex,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun deletePhoto(imageIndex: Int) {
        updateState { copy(isLoading = true) }
        val photoPreview = state.value.photos[imageIndex]

        if (photoPreview is PhotoPreview.Remote) {
            val result = deletePhotoUseCase.invoke(photoPreview.id)

            if (result is Response.Success) {
                submitEvent(PhotosPreviewEvents.OnPhotoDeletedSuccessfully)

                // Update UI after photo deletion
                val newPhotos: List<PhotoPreview> = state.value.photos.toMutableList().apply {
                    removeAt(imageIndex)
                }
                updateState { copy(photos = newPhotos, isThereDeletedPhoto = true) }
            } else {
                submitEvent(PhotosPreviewEvents.OnPhotoDeleteFail)
            }
            updateState { copy(isLoading = false) }
        }
    }

    private fun formatRemoteUrl(photoId: String, serverAddress: String): String = "$serverAddress/api/object/$photoId"
}
