package io.photopixels.presentation.screens.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.showToast
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun RegisterScreen(
    onNavigateToLoginScreen: (email: String, password: String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { events ->
            when (events) {
                is RegisterScreenEvents.NavigateToLoginScreen -> {
                    showToast(messageId = R.string.register_account_success, context)
                    onNavigateToLoginScreen(events.email, events.password)
                }
            }
        }
    }

    RegisterScreenContent(viewState, onSubmitActions = { viewModel.submitAction(it) })
}
