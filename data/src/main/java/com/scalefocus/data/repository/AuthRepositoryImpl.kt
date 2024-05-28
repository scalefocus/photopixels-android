package com.scalefocus.data.repository

import com.scalefocus.data.network.BackendApi
import com.scalefocus.data.storage.datastore.AuthDataStore
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.repository.AuthRepository
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
}
