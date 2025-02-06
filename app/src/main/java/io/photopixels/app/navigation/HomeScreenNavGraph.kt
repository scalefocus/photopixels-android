package io.photopixels.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.photopixels.presentation.base.routes.HomeGraph
import io.photopixels.presentation.base.routes.HomeScreens
import io.photopixels.presentation.base.routes.Screen
import io.photopixels.presentation.screens.home.HomeScreen
import io.photopixels.presentation.screens.photos.PhotosPreviewScreen
import io.photopixels.presentation.screens.settings.SettingsScreen

private const val HOME_SHOULD_REFRESH_BACK_RESULT = "shouldRefresh"

internal fun NavGraphBuilder.homeScreenNavGraph(navHostController: NavHostController) {
    navigation<HomeGraph>(startDestination = HomeScreens.Home) {
        homeScreen(navHostController)
        photosPreviewScreen(navHostController)
        settingsScreen(navHostController)
    }
}

internal fun NavGraphBuilder.homeScreen(navHostController: NavHostController) {
    composable<HomeScreens.Home> { navBackResult ->
        val shouldRefresh = navBackResult.savedStateHandle.get<Boolean>(HOME_SHOULD_REFRESH_BACK_RESULT) ?: false
        HomeScreen(
            shouldRefresh = shouldRefresh,
            onNavigateToSyncScreen = {
                // TODO: implement screens
            },
            onNavigateToPreviewPhotosScreen = { thumbnailServerItemId ->
                navHostController.navigate(
                    HomeScreens.PhotosPreview(thumbnailServerItemId)
                )
            }
        )
    }
}

internal fun NavGraphBuilder.photosPreviewScreen(navHostController: NavHostController) {
    composable<HomeScreens.PhotosPreview> {
        PhotosPreviewScreen(onBackPress = { shouldRefresh ->
            navHostController.popBackStack()
            navHostController.currentBackStackEntry
                ?.savedStateHandle
                ?.set(HOME_SHOULD_REFRESH_BACK_RESULT, shouldRefresh)
        })
    }
}

internal fun NavGraphBuilder.settingsScreen(navHostController: NavHostController) {
    composable<HomeScreens.Settings> {
        SettingsScreen(onNavigateToConnectServerScreen = {
            navHostController.navigateWithoutHistory(Screen.ConnectToServer)
        })
    }
}
