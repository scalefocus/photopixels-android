package io.photopixels.presentation.screens.home

sealed class HomeScreenEvents {
    data object RequestStoragePermissionsEvent : HomeScreenEvents()

    data object NavigateToSyncScreenEvent : HomeScreenEvents()

    data class NavigateToPreviewPhotosScreen(val clickedThumbnailId: String) : HomeScreenEvents()
}
