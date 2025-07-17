package io.photopixels.presentation.screens.mediapreview

sealed class MediaPreviewEvents {
    data object OnMediaDeletedSuccessfully : MediaPreviewEvents()

    data object OnMediaDeleteFail : MediaPreviewEvents()
}
