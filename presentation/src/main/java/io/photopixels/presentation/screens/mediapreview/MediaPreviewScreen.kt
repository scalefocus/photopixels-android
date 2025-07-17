package io.photopixels.presentation.screens.mediapreview

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.showToast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MediaPreviewScreen(onBackPress: (Boolean) -> Unit, viewModel: MediaPreviewViewModel = hiltViewModel()) {
    val screenState = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    // Determine if Home screen should be refreshed when user click back button
    BackHandler {
        if (screenState.isThereDeletedMedia) {
            onBackPress(true)
        } else {
            onBackPress(false)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { events ->
            when (events) {
                MediaPreviewEvents.OnMediaDeletedSuccessfully -> {
                    showToast(messageId = R.string.photos_delete_success, context = context)
                }

                MediaPreviewEvents.OnMediaDeleteFail -> {
                    showToast(messageId = R.string.photos_delete_error, context = context)
                }
            }
        }
    }

    MediaPreviewContent(screenState, onSubmitActions = { action ->
        viewModel.submitAction(action)
    })
}
