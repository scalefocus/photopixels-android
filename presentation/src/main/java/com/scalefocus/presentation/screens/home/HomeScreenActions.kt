package com.scalefocus.presentation.screens.home

sealed class HomeScreenActions {
    data class OnPermissionResult(val permissionsMap: Map<String, Boolean>) : HomeScreenActions()

    data object CloseErrorDialog : HomeScreenActions()

    data object OnGoToSyncButtonClick : HomeScreenActions()

    data object StartSyncWorkers : HomeScreenActions()

    data class OnThumbnailClick(val serverItemId: String) : HomeScreenActions()
}
