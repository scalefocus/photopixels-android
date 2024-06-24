package io.photopixels.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.photopixels.domain.model.AppInfoData
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.SFButton
import io.photopixels.presentation.base.composeviews.SFSwitch
import io.photopixels.presentation.base.composeviews.ShowAlertDialog
import io.photopixels.presentation.theme.AppTypography
import io.photopixels.presentation.theme.PhotoPixelsTheme

@Composable
fun SettingsScreenContent(
    screenState: SettingsScreenState,
    onSubmitAction: (SettingsScreenActions) -> Unit
) {
    val context = LocalContext.current

    screenState.messageId?.let {
        ShowAlertDialog(
            title = stringResource(id = R.string.settings_screen_google_photos_msg),
            negativeButtonText = null,
            description = stringResource(id = it),
            onPositiveClick = { onSubmitAction(SettingsScreenActions.CloseErrorDialog) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.settings_screen_heading_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = AppTypography.HeadingText
        )

        Spacer(modifier = Modifier.height(50.dp))

        screenState.appInfoData?.let { appInfoData ->
            AppDetails(appVersion = appInfoData.appVersion, serverVersion = appInfoData.serverVersion)

            Spacer(modifier = Modifier.height(30.dp))

            ActiveUser(user = appInfoData.loggedUser)

            Spacer(modifier = Modifier.height(30.dp))

            ServerAddress(serverAddress = appInfoData.serverAddress)

            Spacer(modifier = Modifier.height(30.dp))

            BackgroundActivity(
                requirePowerForSync = screenState.userSettings.requirePower,
                requireWifiForSync = screenState.userSettings.requireWifi,
                onPowerForSyncChange = { onSubmitAction(SettingsScreenActions.OnRequirePowerClicked(it)) },
                onWifiForSyncChange = { onSubmitAction(SettingsScreenActions.OnRequireWifiClicked(it)) }
            )

            Spacer(modifier = Modifier.height(30.dp))

            GooglePhotos(
                googlePhotosSyncEnabled = screenState.userSettings.syncWithGoogle,
                onGooglePhotosSyncChange = { checked ->
                    onSubmitAction(
                        SettingsScreenActions.OnSyncGooglePhotosClicked(
                            activityContext = context,
                            isChecked = checked
                        )
                    )
                }
            )

            SFButton(
                text = stringResource(R.string.settings_screen_logout),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                onClick = { onSubmitAction(SettingsScreenActions.OnLogoutClicked) }
            )
        }
    }
}

@Composable
private fun AppDetails(
    appVersion: String,
    serverVersion: String
) {
    Column {
        Text(text = stringResource(R.string.settings_screen_app_details), style = AppTypography.BigBlue)

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(5.dp)) {
                Text(text = stringResource(R.string.settings_screen_version, appVersion))
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                Text(
                    text = stringResource(
                        R.string.settings_screen_server_version,
                        serverVersion
                    )
                )
            }
        }
    }
}

@Composable
private fun ActiveUser(user: String) {
    Column {
        Text(text = stringResource(R.string.settings_screen_active_user), style = AppTypography.BigBlue)

        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(5.dp)) {
                Text(text = user)
            }
        }
    }
}

@Composable
private fun ServerAddress(serverAddress: String) {
    Column {
        Text(text = stringResource(R.string.settings_screen_server_address), style = AppTypography.BigBlue)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(5.dp)) {
                Text(text = serverAddress)
            }
        }
    }
}

@Composable
private fun BackgroundActivity(
    requirePowerForSync: Boolean,
    requireWifiForSync: Boolean,
    onPowerForSyncChange: (Boolean) -> Unit,
    onWifiForSyncChange: (Boolean) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.settings_screen_background_activity), style = AppTypography.BigBlue)

        Card {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                SFSwitch(
                    text = stringResource(R.string.settings_screen_require_power_for_sync),
                    checked = requirePowerForSync,
                    onValueChange = onPowerForSyncChange
                )

                SFSwitch(
                    text = stringResource(
                        R.string.settings_screen_require_wifi_for_sync
                    ),
                    checked = requireWifiForSync,
                    onValueChange = onWifiForSyncChange
                )
            }
        }
    }
}

@Composable
private fun GooglePhotos(
    googlePhotosSyncEnabled: Boolean,
    onGooglePhotosSyncChange: (Boolean) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.settings_screen_google_photos), style = AppTypography.BigBlue)
        Card {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                SFSwitch(
                    text = stringResource(
                        R.string.settings_screen_google_photos_msg
                    ),
                    checked = googlePhotosSyncEnabled,
                    onValueChange = onGooglePhotosSyncChange
                )
            }
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun SettingsScreenContentPreview() {
    PhotoPixelsTheme {
        SettingsScreenContent(
            SettingsScreenState(
                appInfoData = AppInfoData(
                    "google.com",
                    "server-version",
                    "1.0",
                    "john@gmail.com"
                )
            ),
            onSubmitAction = {}
        )
    }
}
