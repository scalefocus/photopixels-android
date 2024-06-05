package com.scalefocus.photopixels.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.scalefocus.presentation.base.Constants
import com.scalefocus.presentation.base.addArgumentLabels
import com.scalefocus.presentation.base.addArguments
import com.scalefocus.presentation.screens.home.HomeScreen
import com.scalefocus.presentation.screens.photos.PhotosPreviewScreen
import com.scalefocus.presentation.screens.settings.SettingsScreen

internal fun NavGraphBuilder.homeScreenNavGraph(navHostController: NavHostController) {
    navigation(startDestination = HomeScreens.Home.route, route = HomeScreens.HOME_GRAPH_ROUTE) {
        homeScreen(navHostController)
        photosPreviewScreen(navHostController)
        settingsScreen(navHostController)
    }
}

internal fun NavGraphBuilder.homeScreen(navHostController: NavHostController) {
    composable(route = HomeScreens.Home.route) {
        HomeScreen(
            onNavigateToSyncScreen = {
                // TODO: implement screens
            },
            onNavigateToPreviewPhotosScreen = { thumbnailServerItemId ->
                navHostController.navigate(
                    HomeScreens.PhotosPreview.route.addArguments(listOf(thumbnailServerItemId))
                )
            }
        )
    }
}

internal fun NavGraphBuilder.photosPreviewScreen(
    @Suppress("UnusedParameter") navHostController: NavHostController
) {
    composable(
        route = HomeScreens.PhotosPreview.route.addArgumentLabels(listOf(Constants.THUMBNAIL_ID_ARGUMENT_NAME)),
        arguments = listOf(
            navArgument(name = Constants.THUMBNAIL_ID_ARGUMENT_NAME) {
                type = NavType.StringArrayType
                nullable = false
            }
        )
    ) {
        PhotosPreviewScreen()
    }
}

internal fun NavGraphBuilder.settingsScreen(navHostController: NavHostController) {
    composable(route = HomeScreens.Settings.route) {
        SettingsScreen(onNavigateToConnectServerScreen = {
            navHostController.navigateWithoutHistory(Screen.ConnectToServer.route)
        })
    }
}
