package com.scalefocus.presentation.screens.login

sealed class LoginScreenEvents {
    data object NavigateToHomeScreen : LoginScreenEvents()
}
