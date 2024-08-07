package io.photopixels.domain.repository

import io.photopixels.domain.base.Response
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun loginUser(email: String, password: String): Flow<Response<Unit>>

    suspend fun clearBearerTokens()

    suspend fun registerUser(name: String, email: String, password: String): Flow<Response<Unit>>

    suspend fun getAuthToken(): String?

    suspend fun getUsername(): String?

    suspend fun clearUserData()

    suspend fun storeGoogleAuthToken(googleAuthToken: String)

    suspend fun getGoogleAuthToken(): String?

    suspend fun clearGoogleAuthToken()

    suspend fun forgotPassword(email: String): Response<Unit>

    suspend fun resetPassword(email: String, newPassword: String, verificationCode: String): Response<Unit>

    suspend fun storeGoogleAuthState(authState: String)

    suspend fun getGoogleAuthState(): String?

    suspend fun clearGoogleAuthState()
}
