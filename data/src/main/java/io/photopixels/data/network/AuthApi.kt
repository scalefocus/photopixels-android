package io.photopixels.data.network

import io.photopixels.data.network.responses.LoginResponse
import io.photopixels.domain.base.Response

interface AuthApi {

    suspend fun loginUser(email: String, password: String): Response<LoginResponse>

    suspend fun refreshToken(refreshToken: String): Response<LoginResponse>
}
