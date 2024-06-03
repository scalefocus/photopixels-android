package com.scalefocus.presentation.screens.forgotpassword.resset

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun ForgotPasswordCodeScreen(
    onNavigateToLoginScreen: () -> Unit,
    viewModel: ForgotPasswordCodeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                ForgotPasswordCodeEvents.NavigateToLoginScreen -> onNavigateToLoginScreen()
            }
        }
    }

    ForgotPasswordCodeContent(state = state, onSubmitActions = {
        viewModel.submitAction(it)
    })
}
