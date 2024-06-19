package io.photopixels.domain.usecases

import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class RemoveGoogleAuthTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend fun invoke() {
        authRepository.clearGoogleAuthToken()
    }
}
