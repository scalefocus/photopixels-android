package com.scalefocus.presentation.screens.forgotpassword.mail

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Suppress("UnusedParameter")
@Composable
fun ForgotPasswordMailScreen(
    onNavigateToVerificationCodeScreen: () -> Unit,
    viewModel: ForgotPasswordMailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    ForgotPasswordMailContent(state, onSubmitActions = {
        viewModel.submitAction(it)
    })
}
