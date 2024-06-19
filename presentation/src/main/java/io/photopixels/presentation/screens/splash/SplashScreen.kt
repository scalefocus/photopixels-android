package io.photopixels.presentation.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun SplashScreen(
    onNavigateToConnectServerScreen: () -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SplashScreenEvents.NavigateToConnectServerScreen -> onNavigateToConnectServerScreen()
                is SplashScreenEvents.NavigateToHomeScreen -> onNavigateToHomeScreen()
            }
        }
    }

    SplashScreenContent(screenState)
}
