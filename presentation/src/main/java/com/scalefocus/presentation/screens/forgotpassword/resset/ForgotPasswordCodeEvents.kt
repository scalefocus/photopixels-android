package com.scalefocus.presentation.screens.forgotpassword.resset

sealed class ForgotPasswordCodeEvents {
    data object NavigateToLoginScreen : ForgotPasswordCodeEvents()
}
