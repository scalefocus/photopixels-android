package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.AuthRepository
import javax.inject.Inject

class SaveGoogleAuthTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun invoke(googleAuthToken: String) {
        authRepository.storeGoogleAuthToken(googleAuthToken)
    }
}
