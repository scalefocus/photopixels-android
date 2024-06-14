package io.photopixels.data.repository

import io.photopixels.data.network.BackendApi
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.domain.base.Response
import io.photopixels.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override suspend fun loginUser(email: String, password: String): Flow<Response<Unit>> =
        flow {
            val result = backendApi.loginUser(email, password)
            if (result is Response.Success) {
                authDataStore.storeUsername(email)
                authDataStore.storeAuthHeaders(result.result.accessToken, refreshToken = result.result.refreshToken)
                emit(Response.Success(Unit))
            } else if (result is Response.Failure) {
                emit(result)
            }
        }

    override suspend fun clearBearerTokens() {
        backendApi.clearBearerTokens()
    }

    override suspend fun registerUser(name: String, email: String, password: String): Flow<Response<Unit>> =
        flow {
            val result = backendApi.registerUser(name, email, password)
            if (result is Response.Success) {
                emit(Response.Success(Unit))
            } else if (result is Response.Failure) {
                emit(result)
            }
        }

    override suspend fun getAuthToken(): String? {
        return authDataStore.getAuthToken()
    }

    override suspend fun getUsername(): String? {
        return authDataStore.getUsername()
    }

    override suspend fun clearUserData() {
        authDataStore.clearUserData()
    }

    override suspend fun storeGoogleAuthToken(googleAuthToken: String) {
        authDataStore.storeGoogleAuthToken(googleAuthToken)
    }

    override suspend fun getGoogleAuthToken(): String? {
        return authDataStore.getGoogleAuthToken()
    }

    override suspend fun clearGoogleAuthToken() {
        authDataStore.clearGoogleAuthToken()
    }

    override suspend fun forgotPassword(email: String): Response<Unit> {
        return backendApi.forgotPassword(email)
    }

    override suspend fun resetPassword(email: String, newPassword: String, verificationCode: String): Response<Unit> {
        return backendApi.resetPassword(email, newPassword, verificationCode)
    }
}
