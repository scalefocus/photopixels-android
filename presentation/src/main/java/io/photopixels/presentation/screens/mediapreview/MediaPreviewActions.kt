package io.photopixels.presentation.screens.mediapreview

sealed class MediaPreviewActions {
    data object OnDeleteIconClicked : MediaPreviewActions()

    data class OnDeleteMediaClick(
        val mediaIndex: Int
    ) : MediaPreviewActions()

    data object OnDeleteDialogCancelClick : MediaPreviewActions()
}
