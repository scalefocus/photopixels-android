package io.photopixels.presentation.screens.photos

sealed class PhotosPreviewActions {
    data object OnDeleteIconClicked : PhotosPreviewActions()

    data class OnDeletePhotoClick(
        val photoIndex: Int
    ) : PhotosPreviewActions()

    data object OnDeleteDialogCancelClick : PhotosPreviewActions()
}
