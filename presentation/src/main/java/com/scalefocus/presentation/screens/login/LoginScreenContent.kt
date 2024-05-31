package com.scalefocus.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scalefocus.presentation.R
import com.scalefocus.presentation.base.composeviews.CircularIndicator
import com.scalefocus.presentation.base.composeviews.SFButton
import com.scalefocus.presentation.base.composeviews.SFPasswordTextField
import com.scalefocus.presentation.base.composeviews.ShowAlertDialog
import com.scalefocus.presentation.theme.AppTypography
import com.scalefocus.presentation.theme.PhotoPixelsTheme

@Suppress("LongMethod")
@Composable
fun LoginScreenContent(
    state: LoginScreenState,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToForgotPassScreen: () -> Unit,
    onSubmitActions: (LoginScreenActions) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.email, state.password) {
        username = state.email
        password = state.password
    }

    if (state.isLoading) {
        CircularIndicator()
    }

    state.errorMsgId?.let {
        ShowAlertDialog(
            title = stringResource(id = R.string.login_error_title),
            negativeButtonText = null,
            description = stringResource(id = it),
            onPositiveClick = { onSubmitActions(LoginScreenActions.CloseErrorDialog) }
        )
    }

    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.background),
            alpha = 0.4f,
            contentScale = ContentScale.Crop,
            contentDescription = "background"
        )

        Box(contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(100.dp))
                Image(painter = painterResource(id = R.drawable.logophotopixels2_light), contentDescription = "logo")

                Spacer(Modifier.height(10.dp))

                Text(text = stringResource(R.string.login_hint_text).uppercase())

                Spacer(Modifier.height(5.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = username,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.login_username)) }
                )

                SFPasswordTextField(password = password, onValueChange = {
                    password = it
                })

                Spacer(Modifier.height(30.dp))

                SFButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSubmitActions(LoginScreenActions.LoginAction(username, password)) },
                    enabled = username.isNotEmpty() && password.isNotEmpty(),
                    text = stringResource(id = R.string.login_btn)
                )

                Spacer(Modifier.height(10.dp))

                SFButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToRegisterScreen() },
                    enabled = true, // TODO
                    text = stringResource(id = R.string.register_btn)
                )

                Spacer(Modifier.height(30.dp))

                Text(
                    text = stringResource(id = R.string.forgot_pass_title),
                    style = AppTypography.SmallBlue,
                    modifier = Modifier.clickable { onNavigateToForgotPassScreen() }
                )

                Spacer(Modifier.height(30.dp))

                Text(
                    text = stringResource(id = R.string.login_go_back_msg),
                    style = AppTypography.SmallBlue,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 33)
private fun PreviewLoginScreenContent() {
    PhotoPixelsTheme {
        LoginScreenContent(
            state = LoginScreenState(),
            onNavigateToRegisterScreen = {},
            onSubmitActions = {},
            onNavigateToForgotPassScreen = {},
            onNavigateBack = {}
        )
    }
}
