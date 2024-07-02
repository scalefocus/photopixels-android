package io.photopixels.domain.usecases

import io.photopixels.domain.model.UserSettings
import io.photopixels.domain.repository.UserSettingsRepository
import javax.inject.Inject

data class SetUserSettingsUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend fun invoke(userSettings: UserSettings) {
        userSettingsRepository.setUserSettings(userSettings)
    }
}
