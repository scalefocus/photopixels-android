package io.photopixels.presentation.screens.settings

import androidx.annotation.StringRes
import io.photopixels.domain.model.AppInfoData

data class SettingsScreenState(
    val appInfoData: AppInfoData? = null,
    @StringRes val messageId: Int? = null,
    val isGoogleSyncEnabled: Boolean = false,
    val isRequirePowerEnabled: Boolean = false,
    val isRequireWifiEnabled: Boolean = false
)
