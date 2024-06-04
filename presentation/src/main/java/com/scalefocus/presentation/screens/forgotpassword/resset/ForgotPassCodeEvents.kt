package com.scalefocus.presentation.screens.forgotpassword.resset

sealed class ForgotPassCodeEvents {
    data object NavigateToLoginScreen : ForgotPassCodeEvents()
}
