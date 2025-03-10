package io.photopixels.presentation.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
            viewModel.submitAction(HomeScreenActions.OnPermissionResult(permissionsMap))
        }
    )

    val pickedMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
//        submitAction(viewModel, HomeScreenActions.OnPickedMedia(uris))
    }

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

                        pickedMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
