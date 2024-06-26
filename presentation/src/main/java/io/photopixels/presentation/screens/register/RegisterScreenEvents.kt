package io.photopixels.presentation.screens.register

sealed class RegisterScreenEvents {
    data class NavigateToLoginScreen(val email: String, val password: String) : RegisterScreenEvents()
}
