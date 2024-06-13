package io.photopixels.domain.usecases

import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class SaveGoogleAuthTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun invoke(googleAuthToken: String) {
        authRepository.storeGoogleAuthToken(googleAuthToken)
    }
}
