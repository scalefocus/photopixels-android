package io.photopixels.presentation.screens.photos

sealed class PhotosPreviewEvents {
    data object OnPhotoDeletedSuccessfully : PhotosPreviewEvents()

    data object OnPhotoDeleteFail : PhotosPreviewEvents()
}
