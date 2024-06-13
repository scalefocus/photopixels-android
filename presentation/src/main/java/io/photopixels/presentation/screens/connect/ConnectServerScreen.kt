package io.photopixels.presentation.screens.connect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun ConnectServerScreen(
    onNavigateToLoginScreen: () -> Unit,
    viewModel: ConnectServerViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { events ->
            when (events) {
                is ConnectServerEvents.NavigateToLoginScreen -> {
                    onNavigateToLoginScreen()
                }
            }
        }
    }

    ConnectServerContent(state = state, onSubmitAction = { viewModel.submitAction(it) })
}
