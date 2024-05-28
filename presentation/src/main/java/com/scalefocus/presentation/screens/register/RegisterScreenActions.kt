package com.scalefocus.presentation.screens.register

sealed class RegisterScreenActions {
    data class RegisterAction(val name: String, val email: String, val password: String) : RegisterScreenActions()

    data object CloseErrorDialog : RegisterScreenActions()
}
