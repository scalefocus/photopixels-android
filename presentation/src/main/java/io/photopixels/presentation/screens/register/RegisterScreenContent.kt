package io.photopixels.presentation.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.CircularIndicator
import io.photopixels.presentation.base.composeviews.SFButton
import io.photopixels.presentation.base.composeviews.SFDefaultTextField
import io.photopixels.presentation.base.composeviews.SFPasswordTextField
import io.photopixels.presentation.base.composeviews.ShowAlertDialog
import io.photopixels.presentation.theme.PhotoPixelsTheme

@Suppress("LongMethod")
@Composable
fun RegisterScreenContent(
    state: RegisterScreenState,
    onSubmitActions: (RegisterScreenActions) -> Unit
) {
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

    val areFieldsNotEmpty = state.name.value.isNotEmpty() && state.password.value.isNotEmpty() &&
        state.email.value.isNotEmpty() && state.confirmPassword.value.isNotEmpty()

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

                SFDefaultTextField(
                    value = state.name.value,
                    label = stringResource(R.string.register_name),
                    isError = state.name.errorMsgId != null,
                    errorText = state.name.errorMsgId,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        onSubmitActions(RegisterScreenActions.OnNameValueChanged(it))
                    }
                )

                SFDefaultTextField(
                    value = state.email.value,
                    label = stringResource(R.string.register_email),
                    isError = state.email.errorMsgId != null,
                    errorText = state.email.errorMsgId,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        onSubmitActions(RegisterScreenActions.OnEmailValueChanged(it))
                    }
                )

                SFPasswordTextField(
                    password = state.password.value,
                    isError = state.password.errorMsgId != null,
                    errorText = state.password.errorMsgId,
                    imeAction = ImeAction.Next,
                    onValueChange = {
                        onSubmitActions(RegisterScreenActions.OnPasswordValueChanged(it))
                    }
                )

                SFPasswordTextField(
                    password = state.confirmPassword.value,
                    label = stringResource(id = R.string.register_confirm_password),
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = if (areFieldsNotEmpty) {
                            { onSubmitActions(RegisterScreenActions.RegisterAction) }
                        } else {
                            null
                        }
                    ),
                    onValueChange = {
                        onSubmitActions(RegisterScreenActions.OnConfirmPasswordValueChanged(it))
                    }
                )

                Spacer(Modifier.height(30.dp))

                SFButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSubmitActions(RegisterScreenActions.RegisterAction) },
                    enabled = areFieldsNotEmpty,
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
