package com.scalefocus.presentation.screens.settings

import androidx.annotation.StringRes
import com.scalefocus.domain.model.AppInfoData

data class SettingsScreenState(
    val appInfoData: AppInfoData? = null,
    @StringRes val messageId: Int? = null,
    val isGoogleSyncEnabled: Boolean = false,
    val isRequirePowerEnabled: Boolean = false,
    val isRequireWifiEnabled: Boolean = false
)
