package com.scalefocus.presentation.screens.forgotpassword.mail

sealed class ForgotPasswordMailEvents {
    data class NavigateToResetPasswordScreen(val email: String) : ForgotPasswordMailEvents()
}
