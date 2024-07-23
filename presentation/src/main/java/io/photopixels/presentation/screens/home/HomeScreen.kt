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

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsMap ->
            submitAction(viewModel = viewModel, HomeScreenActions.OnPermissionResult(permissionsMap))
        }
    )

    ObserverLifecycleEvents(onStart = {
        if (shouldRefresh) {
            submitAction(viewModel, HomeScreenActions.LoadStartupData)
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
            submitAction(viewModel, action)
        }
    )
}

private fun submitAction(viewModel: HomeScreenViewModel, actions: HomeScreenActions) {
    viewModel.submitAction(actions)
}
