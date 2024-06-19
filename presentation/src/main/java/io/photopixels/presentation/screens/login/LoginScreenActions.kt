package io.photopixels.presentation.screens.login

sealed class LoginScreenActions {
    data class LoginAction(val email: String, val password: String) : LoginScreenActions()

    data object CloseErrorDialog : LoginScreenActions()
}
