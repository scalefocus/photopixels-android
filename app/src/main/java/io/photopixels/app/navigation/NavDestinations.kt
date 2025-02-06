package io.photopixels.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.photopixels.presentation.base.routes.Screen
import io.photopixels.presentation.screens.connect.ConnectServerScreen
import io.photopixels.presentation.screens.forgotpassword.mail.ForgotPassMailScreen
import io.photopixels.presentation.screens.forgotpassword.resset.ForgotPassCodeScreen
import io.photopixels.presentation.screens.login.LoginScreen
import io.photopixels.presentation.screens.register.RegisterScreen
import io.photopixels.presentation.screens.splash.SplashScreen

internal fun NavGraphBuilder.splashScreen(navController: NavHostController) {
    composable<Screen.Splash> {
        SplashScreen(onNavigateToConnectServerScreen = {
            navController.popBackStack()
            navController.navigate(Screen.ConnectToServer)
        }, {
            navigateToHomeScreen(navController)
        })
    }
}

internal fun NavGraphBuilder.connectToServerScreen(navController: NavHostController) {
    composable<Screen.ConnectToServer> {
        ConnectServerScreen(onNavigateToLoginScreen = {
            navController.navigate(Screen.Login())
        })
    }
}

internal fun NavGraphBuilder.loginScreen(navController: NavHostController) {
    composable<Screen.Login> {
        LoginScreen(
            onNavigateToRegisterScreen = { navController.navigate(Screen.Register) },
            onNavigateToHomeScreen = { navigateToHomeScreen(navController) },
            onNavigateToForgotPassScreen = { navController.navigate(Screen.ForgotPasswordMail) },
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

internal fun NavGraphBuilder.registerScreen(navController: NavHostController) {
    composable<Screen.Register> {
        RegisterScreen(onNavigateToLoginScreen = { email, password ->
            navController.navigate(Screen.Login(email = email, password = password))
        })
    }
}

internal fun NavGraphBuilder.forgotPassMailScreen(navController: NavHostController) {
    composable<Screen.ForgotPasswordMail> {
        ForgotPassMailScreen(onNavigateToVerificationCodeScreen = { email ->
            navController.popBackStack()
            navController.navigate(Screen.ForgotPasswordCode(email))
        })
    }
}

internal fun NavGraphBuilder.forgotPassCodeScreen(navController: NavHostController) {
    composable<Screen.ForgotPasswordCode> {
        ForgotPassCodeScreen(onNavigateToLoginScreen = {
            navController.popBackStack()
            navController.navigate(Screen.Login()) {
                launchSingleTop = true
            }
        })
    }
}
