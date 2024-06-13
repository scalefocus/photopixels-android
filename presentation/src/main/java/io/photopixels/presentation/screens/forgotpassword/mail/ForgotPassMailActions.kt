package io.photopixels.presentation.screens.forgotpassword.mail

sealed class ForgotPassMailActions {
    data object OnSubmitClicked : ForgotPassMailActions()

    data object CloseErrorDialog : ForgotPassMailActions()

    data class OnEmailValueChanged(val email: String) : ForgotPassMailActions()
}
