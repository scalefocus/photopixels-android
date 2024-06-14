package io.photopixels.presentation.screens.login

sealed class LoginScreenEvents {
    data object NavigateToHomeScreen : LoginScreenEvents()
}
