package io.photopixels.domain.usecases.auth

import io.photopixels.domain.base.Response
import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend fun invoke(name: String, email: String, password: String): Response<Unit> {
        return authRepository.registerUser(name, email, password)
    }
}
