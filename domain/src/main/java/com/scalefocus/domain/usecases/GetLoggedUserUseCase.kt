package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.AuthRepository
import javax.inject.Inject

class GetLoggedUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun invoke(): String? {
        return authRepository.getUsername()
    }
}
