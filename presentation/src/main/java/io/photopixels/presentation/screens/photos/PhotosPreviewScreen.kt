package io.photopixels.presentation.screens.photos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.showToast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PhotosPreviewScreen(viewModel: PhotosPreviewViewModel = hiltViewModel()) {
    val screenState = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { events ->
            when (events) {
                PhotosPreviewEvents.OnPhotoDeletedSuccessfully -> {
                    showToast(messageId = R.string.photos_delete_success, context = context)
                }

                PhotosPreviewEvents.OnPhotoDeleteFail -> {
                    showToast(messageId = R.string.photos_delete_error, context = context)
                }
            }
        }
    }

    PhotosPreviewContent(screenState, onSubmitActions = { action ->
        viewModel.submitAction(action)
    })
}
