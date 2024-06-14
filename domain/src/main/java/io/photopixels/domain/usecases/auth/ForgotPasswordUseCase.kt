package io.photopixels.domain.usecases.auth

import io.photopixels.domain.base.Response
import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun forgotPassword(email: String): Response<Unit> {
        return authRepository.forgotPassword(email)
    }

    suspend fun resetPassword(email: String, newPassword: String, verificationCode: String): Response<Unit> {
        return authRepository.resetPassword(email, newPassword, verificationCode)
    }
}
