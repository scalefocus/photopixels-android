package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.AuthRepository
import javax.inject.Inject

class RemoveGoogleAuthTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend fun invoke() {
        authRepository.clearGoogleAuthToken()
    }
}
