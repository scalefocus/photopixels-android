package com.scalefocus.presentation.screens.forgotpassword.mail

sealed class ForgotPasswordMailEvents {
    data object NavigateToResetPasswordScreen : ForgotPasswordMailEvents()
}
