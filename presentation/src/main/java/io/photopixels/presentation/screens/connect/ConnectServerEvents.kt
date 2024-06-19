package io.photopixels.presentation.screens.connect

sealed class ConnectServerEvents {
    data object NavigateToLoginScreen : ConnectServerEvents()
}
