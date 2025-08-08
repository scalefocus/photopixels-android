package io.photopixels.data.repository

import io.photopixels.data.network.AuthApi
import io.photopixels.data.network.BackendApi
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.domain.base.Response
import io.photopixels.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val backendApi: BackendApi,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override suspend fun loginUser(email: String, password: String): Response<Unit> {
        clearBearerTokens() // Clear the old tokens, so the new ones can be loaded on the next request
        val result = authApi.loginUser(email, password)
        return when (result) {
            is Response.Success -> {
                authDataStore.storeUsername(email)
                authDataStore.storeAuthHeaders(result.result.accessToken, refreshToken = result.result.refreshToken)
                Response.Success(Unit)
            }
            is Response.Failure -> result
        }
    }

    override suspend fun clearBearerTokens() {
        backendApi.clearBearerTokens()
    }

    override suspend fun registerUser(name: String, email: String, password: String): Response<Unit> {
        val result = backendApi.registerUser(name, email, password)
        return when (result) {
            is Response.Success -> {
                Response.Success(Unit)
            }
            is Response.Failure -> result
        }
    }

    override suspend fun getUsername(): String? = authDataStore.getUsername()

    override suspend fun clearUserData() {
        authDataStore.clearUserData()
    }

    override suspend fun storeGoogleAuthToken(googleAuthToken: String) {
        authDataStore.storeGoogleAuthToken(googleAuthToken)
    }

    override suspend fun getGoogleAuthToken(): String? = authDataStore.getGoogleAuthToken()

    override suspend fun clearGoogleAuthToken() {
        authDataStore.clearGoogleAuthToken()
    }

    override suspend fun forgotPassword(email: String): Response<Unit> = backendApi.forgotPassword(email)

    override suspend fun resetPassword(email: String, newPassword: String, verificationCode: String): Response<Unit> =
        backendApi.resetPassword(
            email = email,
            newPassword = newPassword,
            verificationCode = verificationCode
        )

    override suspend fun storeGoogleAuthState(authState: String) {
        authDataStore.storeGoogleAuthState(authState)
    }

    override suspend fun getGoogleAuthState(): String? = authDataStore.getGoogleAuthState()

    override suspend fun clearGoogleAuthState() {
        authDataStore.clearGoogleAuthState()
    }
}
