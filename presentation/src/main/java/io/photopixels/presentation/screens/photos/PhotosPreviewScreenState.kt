package io.photopixels.presentation.screens.photos

import io.photopixels.domain.model.MediaType

data class PhotosPreviewScreenState(
    val photoToLoadFirstIndex: Int = 0,
    val photos: List<PhotoPreview> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val isThereDeletedPhoto: Boolean = false
) {
    sealed class PhotoPreview {
        abstract val id: String
        abstract val mediaType: MediaType

        data class Local(
            override val id: String,
            override val mediaType: MediaType,
            val contentUri: String,
        ) : PhotoPreview()

        data class Remote(
            override val id: String,
            override val mediaType: MediaType,
            val photoUrl: String,
        ) : PhotoPreview()
    }
}
