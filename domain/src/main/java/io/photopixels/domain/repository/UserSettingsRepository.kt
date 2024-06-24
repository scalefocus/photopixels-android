package io.photopixels.domain.repository

import io.photopixels.domain.model.UserSettings

interface UserSettingsRepository {
    suspend fun setUserSettings(userSettings: UserSettings)

    suspend fun getUserSettings(): UserSettings?
}
