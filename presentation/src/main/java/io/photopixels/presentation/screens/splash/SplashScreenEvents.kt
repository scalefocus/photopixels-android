package io.photopixels.presentation.screens.splash

sealed class SplashScreenEvents {
    data object NavigateToConnectServerScreen : SplashScreenEvents()

    data object NavigateToHomeScreen : SplashScreenEvents()
}
