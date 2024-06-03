package com.scalefocus.presentation.screens.forgotpassword.mail

sealed class ForgotPassMailEvents {
    data class NavigateToResetPassScreen(val email: String) : ForgotPassMailEvents()
}
