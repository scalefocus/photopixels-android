package io.photopixels.domain.usecases.auth

import io.photopixels.domain.base.Response
import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend fun invoke(email: String, password: String): Response<Unit> {
        return authRepository.loginUser(email, password)
    }
}
