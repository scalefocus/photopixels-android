package io.photopixels.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.photopixels.presentation.base.Constants
import io.photopixels.presentation.base.addArgumentLabels
import io.photopixels.presentation.base.addArguments
import io.photopixels.presentation.base.addNullableArguments
import io.photopixels.presentation.base.addNullableArgumentsLabels
import io.photopixels.presentation.screens.connect.ConnectServerScreen
import io.photopixels.presentation.screens.forgotpassword.mail.ForgotPassMailScreen
import io.photopixels.presentation.screens.forgotpassword.resset.ForgotPassCodeScreen
import io.photopixels.presentation.screens.login.LoginScreen
import io.photopixels.presentation.screens.register.RegisterScreen
import io.photopixels.presentation.screens.splash.SplashScreen

internal fun NavGraphBuilder.splashScreen(navController: NavHostController) {
    composable(route = Screen.Splash.route) {
        SplashScreen(onNavigateToConnectServerScreen = {
            navController.popBackStack()
            navController.navigate(Screen.ConnectToServer.route)
        }, {
            navigateToHomeScreen(navController)
        })
    }
}

internal fun NavGraphBuilder.connectToServerScreen(navController: NavHostController) {
    composable(route = Screen.ConnectToServer.route) {
        ConnectServerScreen(onNavigateToLoginScreen = {
            navController.navigate(Screen.Login.route)
        })
    }
}

internal fun NavGraphBuilder.loginScreen(navController: NavHostController) {
    val emailArg = navArgument(Constants.EMAIL_ARGUMENT_NAME) {
        type = NavType.StringType
        nullable = true
    }

    val passwordArg = navArgument(Constants.PASSWORD_ARGUMENT_NAME) {
        type = NavType.StringType
        nullable = true
    }

    composable(
        route = Screen.Login.route.addNullableArgumentsLabels(
            listOf(
                Constants.EMAIL_ARGUMENT_NAME,
                Constants.PASSWORD_ARGUMENT_NAME
            )
        ),
        arguments = listOf(emailArg, passwordArg)
    ) {
        LoginScreen(
            onNavigateToRegisterScreen = { navController.navigate(Screen.Register.route) },
            onNavigateToHomeScreen = { navigateToHomeScreen(navController) },
            onNavigateToForgotPassScreen = { navController.navigate(Screen.ForgotPasswordMail.route) },
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

internal fun NavGraphBuilder.registerScreen(navController: NavHostController) {
    composable(route = Screen.Register.route) {
        RegisterScreen(onNavigateToLoginScreen = { email, password ->
            val arguments = mapOf(Constants.EMAIL_ARGUMENT_NAME to email, Constants.PASSWORD_ARGUMENT_NAME to password)
            navController.navigate(Screen.Login.route.addNullableArguments(arguments))
        })
    }
}

internal fun NavGraphBuilder.forgotPassMailScreen(navController: NavHostController) {
    composable(route = Screen.ForgotPasswordMail.route) {
        ForgotPassMailScreen(onNavigateToVerificationCodeScreen = { email ->
            navController.popBackStack()
            navController.navigate(Screen.ForgotPasswordCode.route.addArguments(listOf(email)))
        })
    }
}

internal fun NavGraphBuilder.forgotPassCodeScreen(navController: NavHostController) {
    composable(
        route = Screen.ForgotPasswordCode.route.addArgumentLabels(listOf(Constants.EMAIL_ARGUMENT_NAME)),
        arguments = listOf(
            navArgument(name = Constants.EMAIL_ARGUMENT_NAME) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        ForgotPassCodeScreen(onNavigateToLoginScreen = {
            navController.popBackStack()
            navController.navigate(Screen.Login.route) {
                launchSingleTop = true
            }
        })
    }
}
