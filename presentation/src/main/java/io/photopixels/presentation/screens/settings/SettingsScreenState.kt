package io.photopixels.presentation.screens.settings

import androidx.annotation.StringRes
import io.photopixels.domain.model.AppInfoData
import io.photopixels.domain.model.UserSettings

data class SettingsScreenState(
    val appInfoData: AppInfoData? = null,
    @StringRes val messageId: Int? = null,
    val userSettings: UserSettings = UserSettings()
)
