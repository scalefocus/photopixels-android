package io.photopixels.domain.usecases

import io.photopixels.domain.repository.UserSettingsRepository
import javax.inject.Inject

class GetUserSettingsUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend fun invoke() = userSettingsRepository.getUserSettings()
}
