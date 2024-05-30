package com.scalefocus.presentation.screens.forgotpassword.resset

sealed class ForgotPasswordCodeActions {
    data class OnPasswordChange(val password: String) : ForgotPasswordCodeActions()

    data class OnConfirmPasswordChange(val confirmPassword: String) : ForgotPasswordCodeActions()

    data object OnSubmitClicked : ForgotPasswordCodeActions()

    data object CloseErrorDialog : ForgotPasswordCodeActions()
}
