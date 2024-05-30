package com.scalefocus.presentation.screens.forgotpassword.resset

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ForgotPasswordCodeScreen(viewModel: ForgotPasswordCodeViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    ForgotPasswordCodeContent(state = state, onSubmitActions = {
        viewModel.submitAction(it)
    })
}
