package io.photopixels.presentation.screens.photos

import com.bumptech.glide.load.model.GlideUrl

// TODO Add photo urls, and prepare Auth headers in VM
data class PhotosPreviewScreenState(
    val photoToLoadFirstIndex: Int = 0,
    val photos: List<PhotoPreview> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val isThereDeletedPhoto: Boolean = false
) {
    sealed class PhotoPreview {
        abstract val id: String

        data class Local(override val id: String, val contentUri: String) : PhotoPreview()

        data class Remote(override val id: String, val photoUrl: GlideUrl) : PhotoPreview()
    }
}
