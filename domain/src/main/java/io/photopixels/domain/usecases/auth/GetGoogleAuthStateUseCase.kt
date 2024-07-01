package io.photopixels.domain.usecases.auth

import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class GetGoogleAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun invoke(): String? = authRepository.getGoogleAuthState()
}
