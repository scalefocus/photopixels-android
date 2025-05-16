package io.photopixels.presentation.screens.home

import io.photopixels.presentation.permissions.StorageAccess

sealed class HomeScreenActions {
    data class UpdateStorageAccess(val storageAccess: StorageAccess) : HomeScreenActions()

    data class OnPermissionResult(val storageAccess: StorageAccess) : HomeScreenActions()

    data object CloseErrorDialog : HomeScreenActions()

    data object OnSyncButtonClick : HomeScreenActions()

    data object StartSyncWorkers : HomeScreenActions()

    data class OnThumbnailClick(
        val thumbnailId: String
    ) : HomeScreenActions()

    data object LoadStartupData : HomeScreenActions()
}
