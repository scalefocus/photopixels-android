package io.photopixels.presentation.screens.connect

sealed class ConnectServerActions {
    data class ConnectAction(val serverAddress: String) : ConnectServerActions()

    data object CloseErrorDialog : ConnectServerActions()

    data class OnServerValueChanged(val serverAddress: String) : ConnectServerActions()
}
