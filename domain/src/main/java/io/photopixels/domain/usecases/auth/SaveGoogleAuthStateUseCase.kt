package io.photopixels.domain.usecases.auth

import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class SaveGoogleAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun invoke(googleAuthState: String) {
        authRepository.storeGoogleAuthState(googleAuthState)
    }
}
