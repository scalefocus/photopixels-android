package io.photopixels.presentation.screens.mediapreview

import io.photopixels.domain.model.MediaType

data class MediaPreviewScreenState(
    val mediaToLoadFirstIndex: Int = 0,
    val mediaItems: List<MediaPreview> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val isThereDeletedMedia: Boolean = false
) {
    sealed class MediaPreview {
        abstract val id: String
        abstract val mediaType: MediaType

        data class Local(
            override val id: String,
            override val mediaType: MediaType,
            val contentUri: String,
        ) : MediaPreview()

        data class Remote(
            override val id: String,
            override val mediaType: MediaType,
            val mediaUrl: String,
        ) : MediaPreview()
    }
}
