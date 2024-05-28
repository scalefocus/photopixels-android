package com.scalefocus.presentation.screens.photos

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PhotosPreviewScreen(viewModel: PhotosPreviewViewModel = hiltViewModel()) {
    val screenState = viewModel.state.collectAsStateWithLifecycle().value

    PhotosPreviewContent(screenState, onSubmitActions = { action ->
        viewModel.submitAction(action)
    })
}
