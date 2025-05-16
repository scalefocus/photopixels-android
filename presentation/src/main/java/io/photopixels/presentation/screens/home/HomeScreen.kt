package io.photopixels.presentation.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.photopixels.presentation.base.composeviews.ObserverLifecycleEvents
import io.photopixels.presentation.permissions.PermissionsHelper
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun HomeScreen(
    onNavigateToSyncScreen: () -> Unit,
    onNavigateToPreviewPhotosScreen: (String) -> Unit,
    shouldRefresh: Boolean,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val storageAccess = PermissionsHelper.getStorageAccess(context)
        viewModel.submitAction(HomeScreenActions.UpdateStorageAccess(storageAccess))
    }

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ ->
            val storageAccess = PermissionsHelper.getStorageAccess(context)
            viewModel.submitAction(HomeScreenActions.OnPermissionResult(storageAccess))
        }
    )

    ObserverLifecycleEvents(onStart = {
        if (shouldRefresh) {
            viewModel.submitAction(HomeScreenActions.LoadStartupData)
        }
    })

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest {
            when (it) {
                is HomeScreenEvents.RequestStoragePermissionsEvent -> {
                    if (PermissionsHelper.checkAndRequestPermissions(context, requestPermissions)) {
                        viewModel.submitAction(HomeScreenActions.StartSyncWorkers)
                    }
                }

                HomeScreenEvents.NavigateToSyncScreenEvent -> {
                    if (PermissionsHelper.checkAndRequestPermissions(context, requestPermissions)) {
                        onNavigateToSyncScreen()
                    }
                }

                is HomeScreenEvents.NavigateToPreviewPhotosScreen ->
                    onNavigateToPreviewPhotosScreen(it.clickedThumbnailId)
            }
        }
    }

    HomeScreenContent(
        state = state,
        onSubmitActions = { action ->
            viewModel.submitAction(action)
        }
    )
}
