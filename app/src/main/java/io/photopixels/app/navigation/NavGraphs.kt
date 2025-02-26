package io.photopixels.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import io.photopixels.presentation.base.composeviews.EnableScreenOrientation
import io.photopixels.presentation.base.composeviews.SetPortraitOrientationOnly
import io.photopixels.presentation.base.routes.HomeScreens
import io.photopixels.presentation.base.routes.SCREENS_WITH_PORTRAIT_MODE_ONLY
import io.photopixels.presentation.base.routes.Screen
import kotlin.reflect.KClass

@Composable
fun NavGraphs(navController: NavHostController, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (navController.shouldAddBottomNav()) {
                NavigationBar(navHostController = navController)
            }
        }
    ) { paddings ->
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        currentDestination?.let {
            HandleScreenOrientation(it)
        }

        NavHost(
            navController = navController,
            modifier = Modifier.padding(paddings),
            startDestination = Screen.Splash
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
    var currentRoute: HomeScreens by remember {
        mutableStateOf(HomeScreens.Home)
    } // Initially set to Home

    NavigationBar {
        navigationBarItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelRes)) },
                selected = currentRoute == item.route,
                onClick = {
                    val routeToOpen = item.route
                    currentRoute = routeToOpen

                    if (navHostController.currentBackStackEntry?.destination?.route ==
                        routeToOpen::class.simpleName
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
private fun NavHostController.shouldAddBottomNav(): Boolean {
    val navBackStackEntry by currentBackStackEntryAsState()
    val bottomBarsRoutes = remember(navigationBarItems) { navigationBarItems.map { it.route::class } }
    return navBackStackEntry?.destination?.hasRouteIn(bottomBarsRoutes) ?: false
}

@Composable
private fun HandleScreenOrientation(currentDestination: NavDestination) {
    val disableLandscapeMode = currentDestination.hasRouteIn(SCREENS_WITH_PORTRAIT_MODE_ONLY)
    if (disableLandscapeMode) {
        SetPortraitOrientationOnly()
    } else {
        EnableScreenOrientation()
    }
}

private fun NavDestination.hasRouteIn(
    routes: List<KClass<out Any>>
): Boolean = routes.any { route -> hasRoute(route) }

internal fun NavHostController.navigateWithoutHistory(destinationRoute: Any) {
    navigate(destinationRoute) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

internal fun navigateToHomeScreen(navHostController: NavHostController) {
    navHostController.apply {
        navigate(HomeScreens.Home) {
            popUpTo(graph.id) {
                // Remove history
                inclusive = true
            }

            launchSingleTop
        }
    }
}
