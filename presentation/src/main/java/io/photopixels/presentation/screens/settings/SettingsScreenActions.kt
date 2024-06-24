package io.photopixels.presentation.screens.settings

import android.content.Context
import android.content.Intent

sealed class SettingsScreenActions {
    data class LoadSettingsData(
        val appVersion: String
    ) : SettingsScreenActions()

    data object OnLogoutClicked : SettingsScreenActions()

    data class OnSyncGooglePhotosClicked(
        val activityContext: Context,
        val isChecked: Boolean
    ) : SettingsScreenActions()

    data class OnRequireWifiClicked(
        val isChecked: Boolean
    ) : SettingsScreenActions()

    data class OnRequirePowerClicked(
        val isChecked: Boolean
    ) : SettingsScreenActions()

    data object CloseErrorDialog : SettingsScreenActions()

    data class OnGoogleOauthIntentReceived(
        val intent: Intent
    ) : SettingsScreenActions()
}
