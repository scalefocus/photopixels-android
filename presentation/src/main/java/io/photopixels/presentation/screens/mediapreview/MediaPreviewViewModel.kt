package io.photopixels.presentation.screens.mediapreview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.usecases.DeleteMediaUseCase
import io.photopixels.domain.usecases.GetServerInfoUseCase
import io.photopixels.domain.usecases.GetThumbnailsFromDbUseCase
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.base.routes.HomeScreens
import io.photopixels.presentation.screens.mediapreview.MediaPreviewScreenState.MediaPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaPreviewViewModel @Inject constructor(
    private val getThumbnailsUseCase: GetThumbnailsFromDbUseCase,
    private val getServerInfoUseCase: GetServerInfoUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    savedState: SavedStateHandle
) : BaseViewModel<MediaPreviewScreenState, MediaPreviewActions, MediaPreviewEvents>(MediaPreviewScreenState()) {

    init {
        val route = savedState.toRoute<HomeScreens.MediaPreview>()
        prepareMediaUrls(route.thumbnailServerItemId)
    }

    override suspend fun handleActions(action: MediaPreviewActions) {
        when (action) {
            MediaPreviewActions.OnDeleteIconClicked -> {
                updateState { copy(isDeleteDialogVisible = true) }
            }
            is MediaPreviewActions.OnDeleteMediaClick -> {
                updateState { copy(isDeleteDialogVisible = false) }
                deleteMedia(action.mediaIndex)
            }

            MediaPreviewActions.OnDeleteDialogCancelClick -> {
                updateState { copy(isDeleteDialogVisible = false) }
            }
        }
    }

    private fun prepareMediaUrls(clickedThumbnailId: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            val serverAddress = getServerInfoUseCase.getServerAddress()?.toString()
            serverAddress?.let {
                val thumbnails = getThumbnailsUseCase.invoke().first()
                val photos = thumbnails.map {
                    when (it) {
                        is Thumbnail.LocalThumbnail -> MediaPreview.Local(it.id, it.mediaType, it.contentUri)
                        is Thumbnail.RemoteThumbnail -> {
                            MediaPreview.Remote(
                                id = it.id,
                                mediaType = it.mediaType,
                                mediaUrl = formatRemoteUrl(it.id, serverAddress),
                            )
                        }
                    }
                }
                val photoToLoadFirstIndex = thumbnails.indexOfFirst { it.id == clickedThumbnailId }
                updateState {
                    copy(
                        mediaItems = photos,
                        mediaToLoadFirstIndex = photoToLoadFirstIndex,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun deleteMedia(imageIndex: Int) {
        updateState { copy(isLoading = true) }
        val mediaItem = state.value.mediaItems[imageIndex]

        if (mediaItem is MediaPreview.Remote) {
            val result = deleteMediaUseCase.invoke(mediaItem.id)

            if (result is Response.Success) {
                submitEvent(MediaPreviewEvents.OnMediaDeletedSuccessfully)

                // Update UI after photo deletion
                val newPhotos: List<MediaPreview> = state.value.mediaItems.toMutableList().apply {
                    removeAt(imageIndex)
                }
                updateState { copy(mediaItems = newPhotos, isThereDeletedMedia = true) }
            } else {
                submitEvent(MediaPreviewEvents.OnMediaDeleteFail)
            }
            updateState { copy(isLoading = false) }
        }
    }

    private fun formatRemoteUrl(photoId: String, serverAddress: String): String = "$serverAddress/api/object/$photoId"
}
