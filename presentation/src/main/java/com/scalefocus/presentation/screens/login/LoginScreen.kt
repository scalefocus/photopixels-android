package com.scalefocus.presentation.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun LoginScreen(
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { events ->
            when (events) {
                is LoginScreenEvents.NavigateToHomeScreen -> {
                    onNavigateToHomeScreen()
                }
            }
        }
    }

    LoginScreenContent(
        state = viewState,
        onNavigateToRegisterScreen = onNavigateToRegisterScreen,
        onSubmitActions = { viewModel.submitAction(it) },
        onNavigateBack = onNavigateBack
    )
}
