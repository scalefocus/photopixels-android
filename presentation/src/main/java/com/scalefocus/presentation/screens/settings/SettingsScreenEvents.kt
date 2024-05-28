package com.scalefocus.presentation.screens.settings

import android.content.Intent

sealed class SettingsScreenEvents {
    data object NavigateToConnectServerScreen : SettingsScreenEvents()

    data class StartAuthorizationIntent(val authorizationIntent: Intent) : SettingsScreenEvents()
}
