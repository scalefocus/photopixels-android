package io.photopixels.presentation.screens.forgotpassword.resset

sealed class ForgotPassCodeActions {
    data class OnPassChange(val password: String) : ForgotPassCodeActions()

    data class OnConfirmPassChange(val confirmPassword: String) : ForgotPassCodeActions()

    data class OnSubmitClicked(val verificationCode: String) : ForgotPassCodeActions()

    data object CloseErrorDialog : ForgotPassCodeActions()
}
