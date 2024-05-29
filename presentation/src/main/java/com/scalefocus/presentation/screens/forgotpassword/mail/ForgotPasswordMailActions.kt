package com.scalefocus.presentation.screens.forgotpassword.mail

sealed class ForgotPasswordMailActions {
    data object OnSubmitClicked : ForgotPasswordMailActions()

    data object CloseErrorDialog : ForgotPasswordMailActions()

    data class OnEmailValueChanged(val email: String) : ForgotPasswordMailActions()
}
