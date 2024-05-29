package com.scalefocus.presentation.screens.forgotpassword.mail

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scalefocus.presentation.R
import com.scalefocus.presentation.base.composeviews.CircularIndicator
import com.scalefocus.presentation.base.composeviews.SFButton
import com.scalefocus.presentation.base.composeviews.SFDefaultTextField
import com.scalefocus.presentation.base.composeviews.ShowAlertDialog
import com.scalefocus.presentation.theme.PhotoPixelsTheme

@Composable
fun ForgotPasswordMailContent(state: ForgotPasswordMailState, onSubmitActions: (ForgotPasswordMailActions) -> Unit) {
    if (state.isLoading) {
        CircularIndicator()
    }

    state.errorMsgId?.let {
        ShowAlertDialog(
            title = stringResource(id = R.string.login_error_title),
            negativeButtonText = null,
            description = stringResource(id = it),
            onPositiveClick = { onSubmitActions(ForgotPasswordMailActions.CloseErrorDialog) }
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

                Text(text = stringResource(R.string.forgot_pass_screen_hint).uppercase())

                Spacer(Modifier.height(5.dp))

                SFDefaultTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email.value,
                    onValueChange = { onSubmitActions(ForgotPasswordMailActions.OnEmailValueChanged(it)) },
                    label = stringResource(R.string.register_email)
                )

                Spacer(Modifier.height(30.dp))

                SFButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSubmitActions(ForgotPasswordMailActions.OnSubmitClicked) },
                    enabled = state.email.value.isNotEmpty(),
                    text = stringResource(id = R.string.button_submit)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewForgotPasswordMailContent() {
    PhotoPixelsTheme {
        ForgotPasswordMailContent(state = ForgotPasswordMailState()) {
        }
    }
}
