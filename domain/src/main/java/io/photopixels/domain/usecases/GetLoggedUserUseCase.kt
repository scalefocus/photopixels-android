package io.photopixels.domain.usecases

import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class GetLoggedUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun invoke(): String? {
        return authRepository.getUsername()
    }
}
