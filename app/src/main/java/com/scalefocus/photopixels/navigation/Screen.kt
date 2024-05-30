package com.scalefocus.photopixels.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")

    data object ConnectToServer : Screen("connect")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object ForgotPasswordMail : Screen("forgot_pass_mail")
}

sealed class HomeScreens(val route: String) {
    data object Home : HomeScreens("home")

    data object Sync : HomeScreens("sync")

    data object Settings : HomeScreens("settings")

    data object PhotosPreview : HomeScreens("preview_photos")

    companion object {
        internal const val HOME_GRAPH_ROUTE = "home_navigation"
    }
}

val SCREENS_WITH_BOTTOM_BAR = listOf(HomeScreens.Home.route, HomeScreens.Sync.route, HomeScreens.Settings.route)
val SCREENS_WITH_PORTRAIT_MODE_ONLY = listOf(Screen.Register.route, Screen.Login.route, Screen.ConnectToServer.route)
