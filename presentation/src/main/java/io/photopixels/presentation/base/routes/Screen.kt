package io.photopixels.presentation.base.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object Splash : Screen()

    @Serializable
    data object ConnectToServer : Screen()

    @Serializable
    data class Login(
        val email: String? = null,
        val password: String? = null,
    ) : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object ForgotPasswordMail : Screen()

    @Serializable
    data class ForgotPasswordCode(val email: String) : Screen()
}

@Serializable
object HomeGraph

@Serializable
sealed class HomeScreens {

    @Serializable
    data object Home : HomeScreens()

    @Serializable
    data object Sync : HomeScreens()

    @Serializable
    data object Settings : HomeScreens()

    @Serializable
    data class PhotosPreview(val thumbnailServerItemId: String) : HomeScreens()
}

val SCREENS_WITH_PORTRAIT_MODE_ONLY = listOf(
    Screen.Register::class,
    Screen.Login()::class,
    Screen.ConnectToServer::class
)
