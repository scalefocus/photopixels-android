package io.photopixels.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import io.photopixels.presentation.R
import io.photopixels.presentation.base.routes.HomeScreens

data class NavigationItem(
    val route: HomeScreens,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
)

val navigationBarItems = listOf(
    NavigationItem(
        route = HomeScreens.Home,
        labelRes = R.string.home_screen_title,
        icon = Icons.Default.Home,
    ),
//    NavigationItem(
//        route = HomeScreens.Sync,
//        labelRes = R.string.sync_screen_title,
//        icon = Icons.Default.Check,
//    ),
    NavigationItem(
        route = HomeScreens.Settings,
        labelRes = R.string.settings_screen_title,
        icon = Icons.Default.Settings,
    ),
)
