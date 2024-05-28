package com.scalefocus.presentation.screens.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun RegisterScreen(
    onNavigateToLoginScreen: (email: String, password: String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { events ->
            when (events) {
                is RegisterScreenEvents.NavigateToLoginScreen -> {
                    onNavigateToLoginScreen(events.email, events.password)
                }
            }
        }
    }

    RegisterScreenContent(viewState, onSubmitActions = { viewModel.submitAction(it) })
}
