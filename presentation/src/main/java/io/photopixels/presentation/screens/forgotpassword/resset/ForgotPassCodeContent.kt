package io.photopixels.presentation.screens.forgotpassword.resset

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.CircularIndicator
import io.photopixels.presentation.base.composeviews.SFButton
import io.photopixels.presentation.base.composeviews.SFDefaultTextField
import io.photopixels.presentation.base.composeviews.SFPasswordTextField
import io.photopixels.presentation.base.composeviews.ShowAlertDialog
import io.photopixels.presentation.theme.PhotoPixelsTheme

@Composable
fun ForgotPassCodeContent(
    state: ForgotPassCodeState,
    onSubmitActions: (ForgotPassCodeActions) -> Unit
) {
    var verificationCode by remember { mutableStateOf("") }

    if (state.isLoading) {
        CircularIndicator()
    }

    state.errorMsgId?.let {
        ShowAlertDialog(
            title = stringResource(id = R.string.reset_password_title),
            negativeButtonText = null,
            description = stringResource(id = it),
            onPositiveClick = { onSubmitActions(ForgotPassCodeActions.CloseErrorDialog) }
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

                Text(text = stringResource(R.string.forgot_pass_code_msg).uppercase(), textAlign = TextAlign.Center)

                Spacer(Modifier.height(5.dp))
                SFDefaultTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = verificationCode,
                    onValueChange = { verificationCode = it },
                    label = stringResource(R.string.forgot_pass_ver_code)
                )

                SFPasswordTextField(
                    password = state.password.value,
                    label = stringResource(id = R.string.forgot_pass_new),
                    isError = state.password.errorMsgId != null,
                    errorText = state.password.errorMsgId,
                    onValueChange = {
                        onSubmitActions(ForgotPassCodeActions.OnPassChange(it))
                    }
                )

                SFPasswordTextField(
                    password = state.confirmPassword.value,
                    label = stringResource(id = R.string.forgot_pass_confirm),
                    isError = state.confirmPassword.errorMsgId != null,
                    errorText = state.confirmPassword.errorMsgId,
                    onValueChange = {
                        onSubmitActions(ForgotPassCodeActions.OnConfirmPassChange(it))
                    }
                )

                SubmitButton(state = state, verificationCode = verificationCode, onSubmitActions = onSubmitActions)

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SubmitButton(
    state: ForgotPassCodeState,
    verificationCode: String,
    onSubmitActions: (ForgotPassCodeActions) -> Unit
) {
    val areFieldsEmpty = verificationCode.isEmpty() && state.password.value.isEmpty() &&
        state.confirmPassword.value.isEmpty()
    SFButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onSubmitActions(ForgotPassCodeActions.OnSubmitClicked(verificationCode)) },
        enabled = !areFieldsEmpty,
        text = stringResource(id = R.string.button_submit)
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewContent() {
    PhotoPixelsTheme {
        ForgotPassCodeContent(state = ForgotPassCodeState(), onSubmitActions = {})
    }
}
