package io.photopixels.presentation.screens.photos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val savedState: SavedStateHandle
) : BaseViewModel<PhotosPreviewScreenState, PhotosPreviewActions, Unit>(PhotosPreviewScreenState()) {

    init {
        viewModelScope.launch {
            savedState.get<Array<String>>(THUMBNAIL_ID_ARGUMENT_NAME)?.let { preparePhotoUrls(it[0]) }
        }
    }

    override suspend fun handleActions(action: PhotosPreviewActions) {
        super.handleActions(action)
    }

    private fun preparePhotoUrls(clickedThumbnailServerId: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            val serverAddress = getServerInfoUseCase.getServerAddress()
            serverAddress?.let {
                val authHeader = getAuthHeaderUseCase.invoke()
                authHeader?.let {
                    val photosIds = getPhotosIdsInMemoryUseCase.invoke()
                    val photosGlideUrls = photosIds.map { buildGlideUrl(it, serverAddress, authHeader) }
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

    private fun buildGlideUrl(photoId: String, serverAddress: String, authHeader: String): GlideUrl {
        return GlideUrl(
            "https://$serverAddress/api/object/$photoId",
            LazyHeaders.Builder()
                .addHeader(
                    "Authorization",
                    "Bearer $authHeader"
                )
                .build()
        )
    }
}
