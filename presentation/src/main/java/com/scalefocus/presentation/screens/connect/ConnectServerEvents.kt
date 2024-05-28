package com.scalefocus.presentation.screens.connect

sealed class ConnectServerEvents {
    data object NavigateToLoginScreen : ConnectServerEvents()
}
