package com.scalefocus.presentation.screens.register

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scalefocus.presentation.R
import com.scalefocus.presentation.base.composeviews.CircularIndicator
import com.scalefocus.presentation.base.composeviews.SFButton
import com.scalefocus.presentation.base.composeviews.ShowAlertDialog
import com.scalefocus.presentation.theme.PhotoPixelsTheme

@Suppress("LongMethod")
@Composable
fun RegisterScreenContent(
    state: RegisterScreenState,
    onSubmitActions: (RegisterScreenActions) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (state.isLoading) {
        CircularIndicator()
    }

    state.errorMsgId?.let {
        ShowAlertDialog(
            title = stringResource(id = R.string.register_error_title),
            negativeButtonText = null,
            description = stringResource(id = it),
            onPositiveClick = { onSubmitActions(RegisterScreenActions.CloseErrorDialog) }
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

                Text(text = stringResource(R.string.register_hint_text).uppercase())

                Spacer(Modifier.height(5.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.register_name)) }
                )

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.register_email)) }
                )

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.register_password)) },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(30.dp))

                // TODO validate fields - name, email and password
                // before executing the request
                SFButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSubmitActions(
                            RegisterScreenActions.RegisterAction(
                                name,
                                email,
                                password
                            )
                        )
                    },
                    enabled = true, // TODO
                    text = stringResource(id = R.string.register_title).uppercase()
                )

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 33)
private fun PreviewRegisterScreenContent() {
    PhotoPixelsTheme {
        RegisterScreenContent(state = RegisterScreenState(), onSubmitActions = {})
    }
}
