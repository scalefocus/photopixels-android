package io.photopixels.data.repository

import io.photopixels.data.storage.datastore.UserPreferencesDataStore
import io.photopixels.domain.model.UserSettings
import io.photopixels.domain.repository.UserSettingsRepository
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val userPrefsDataStore: UserPreferencesDataStore
) : UserSettingsRepository {
    override suspend fun setUserSettings(userSettings: UserSettings) {
        userPrefsDataStore.setUserSettings(userSettings)
    }

    override suspend fun getUserSettings(): UserSettings? = userPrefsDataStore.getUserSettings()
}
