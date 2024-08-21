package io.photopixels.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import io.photopixels.app.navigation.HomeScreens.Companion.HOME_GRAPH_ROUTE
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.EnableScreenOrientation
import io.photopixels.presentation.base.composeviews.SetPortraitOrientationOnly

@Composable
fun NavGraphs(navController: NavHostController, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (isScreenHaveBottomBar(navHostController = navController)) {
                NavigationBar(navHostController = navController)
            }
        }
    ) { paddings ->
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        currentRoute?.let {
            HandleScreenOrientation(it)
        }

        NavHost(
            navController = navController,
            modifier = Modifier.padding(paddings),
            startDestination = Screen.Splash.route
        ) {
            splashScreen(navController)
            connectToServerScreen(navController)
            loginScreen(navController)
            registerScreen(navController)
            forgotPassMailScreen(navController)
            forgotPassCodeScreen(navController)
            homeScreenNavGraph(navController)
        }
    }
}

@Composable
private fun NavigationBar(navHostController: NavHostController) {
    val currentRoute = remember {
        mutableStateOf(HomeScreens.Home.route.toLowerCase(Locale.current))
    } // Initially set to Home

    NavigationBar {
        val navigationIcons = listOf(
            Icons.Default.Home,
            // Icons.Default.Check,
            Icons.Default.Settings
        )
        val labels = listOf(
            stringResource(R.string.home_screen_title),
            // stringResource(R.string.sync_screen_title),
            stringResource(R.string.settings_screen_title)
        )

        navigationIcons.forEachIndexed { index, icon ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(labels[index]) },
                selected = currentRoute.value == labels[index],
                onClick = {
                    val routeToOpen = labels[index].toLowerCase(Locale.current)
                    currentRoute.value = routeToOpen

                    if (navHostController.currentBackStackEntry?.destination?.route ==
                        routeToOpen
                    ) {
                        return@NavigationBarItem
                    }

                    navHostController.navigate(routeToOpen) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navHostController.currentDestination?.parent?.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        // Avoid multiple copies of the same destination when
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun isScreenHaveBottomBar(navHostController: NavHostController): Boolean {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route in SCREENS_WITH_BOTTOM_BAR
}

@Composable
private fun HandleScreenOrientation(currentRoute: String) {
    var disableLandscapeMode = false

    run loop@{
        SCREENS_WITH_PORTRAIT_MODE_ONLY.forEach { route ->
            if (currentRoute.contains(route, true)) {
                disableLandscapeMode = true
                return@loop // stop iterating
            } else {
                disableLandscapeMode = false
                return@forEach // continue
            }
        }
    }

    if (disableLandscapeMode) {
        SetPortraitOrientationOnly()
    } else {
        EnableScreenOrientation()
    }
}

internal fun NavHostController.navigateWithoutHistory(destinationRoute: String) {
    apply {
        navigate(destinationRoute) {
            popUpTo(graph.id) {
                inclusive = true
            }
        }
    }
}

internal fun navigateToHomeScreen(navHostController: NavHostController) {
    navHostController.apply {
        navigate(HOME_GRAPH_ROUTE) {
            popUpTo(graph.id) {
                // Remove history
                inclusive = true
            }

            launchSingleTop
        }
    }
}
