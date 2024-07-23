package io.photopixels.presentation.screens.connect

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.CircularIndicator
import io.photopixels.presentation.base.composeviews.SFButton
import io.photopixels.presentation.base.composeviews.ShowAlertDialog
import io.photopixels.presentation.theme.PhotoPixelsTheme

@Composable
fun ConnectServerContent(state: ConnectServerState, onSubmitAction: (ConnectServerActions) -> Unit) {
    val serverValue = state.serverAddress.value

    if (state.isLoading) {
        CircularIndicator()
    }

    state.errorMsgId?.let {
        ShowAlertDialog(
            title = stringResource(id = R.string.connect_server_error_title),
            negativeButtonText = null,
            description = stringResource(id = it),
            onPositiveClick = { onSubmitAction(ConnectServerActions.CloseErrorDialog) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Image(painter = painterResource(id = R.drawable.logophotopixels2_light), contentDescription = "logo")

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = stringResource(R.string.connect_screen_msg))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = serverValue,
            onValueChange = { onSubmitAction(ConnectServerActions.OnServerValueChanged(it)) },
            label = { Text(stringResource(R.string.connect_server_address)) },
            isError = state.serverAddress.errorMsgId != null,
            supportingText = {
                state.serverAddress.errorMsgId?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error // TODO Access colors from PhotoPixels theme
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(25.dp))
        SFButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSubmitAction(ConnectServerActions.ConnectAction(serverValue)) },
            enabled = serverValue.isNotEmpty() && !state.isLoading,
            text = stringResource(id = R.string.connect_screen_next_btn)
        )
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 33)
private fun PreviewConnectServerContent() {
    PhotoPixelsTheme {
        ConnectServerContent(state = ConnectServerState(), onSubmitAction = {})
    }
}
