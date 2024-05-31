package com.scalefocus.presentation.screens.forgotpassword.mail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun ForgotPasswordMailScreen(
    onNavigateToVerificationCodeScreen: () -> Unit,
    viewModel: ForgotPasswordMailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { events ->
            when (events) {
                is ForgotPasswordMailEvents.NavigateToResetPasswordScreen -> onNavigateToVerificationCodeScreen()
            }
        }
    }

    ForgotPasswordMailContent(state, onSubmitActions = {
        viewModel.submitAction(it)
    })
}
