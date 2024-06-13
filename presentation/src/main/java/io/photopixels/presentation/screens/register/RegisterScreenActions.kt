package io.photopixels.presentation.screens.register

sealed class RegisterScreenActions {
    data object RegisterAction : RegisterScreenActions()

    data object CloseErrorDialog : RegisterScreenActions()

    data class OnNameValueChanged(val name: String) : RegisterScreenActions()

    data class OnEmailValueChanged(val email: String) : RegisterScreenActions()

    data class OnPasswordValueChanged(val password: String) : RegisterScreenActions()

    data class OnConfirmPasswordValueChanged(val confirmPassword: String) : RegisterScreenActions()
}
