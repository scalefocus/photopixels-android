package com.scalefocus.presentation.screens.forgotpassword.mail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun ForgotPassMailScreen(
    onNavigateToVerificationCodeScreen: (String) -> Unit,
    viewModel: ForgotPassMailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ForgotPassMailEvents.NavigateToResetPassScreen -> onNavigateToVerificationCodeScreen(
                    event.email
                )
            }
        }
    }

    ForgotPassMailContent(state, onSubmitActions = {
        viewModel.submitAction(it)
    })
}
