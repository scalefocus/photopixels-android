package com.scalefocus.photopixels.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scalefocus.presentation.base.Constants
import com.scalefocus.presentation.base.addNullableArguments
import com.scalefocus.presentation.base.addNullableArgumentsLabels
import com.scalefocus.presentation.screens.connect.ConnectServerScreen
import com.scalefocus.presentation.screens.forgotpassword.mail.ForgotPasswordMailScreen
import com.scalefocus.presentation.screens.login.LoginScreen
import com.scalefocus.presentation.screens.register.RegisterScreen
import com.scalefocus.presentation.screens.splash.SplashScreen

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

internal fun NavGraphBuilder.forgotPassScreenMail(navController: NavHostController) {
    composable(route = Screen.ForgotPasswordMail.route) {
        ForgotPasswordMailScreen(onNavigateToVerificationCodeScreen = {
            navController.popBackStack()
            navController.navigate(Screen.ForgotPasswordCode.route)
        })
    }
}
