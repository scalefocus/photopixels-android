package com.scalefocus.domain.usecases

import com.scalefocus.domain.base.Response
import com.scalefocus.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend fun invoke(email: String, password: String): Flow<Response<Unit>> {
        return authRepository.loginUser(email, password)
    }
}
