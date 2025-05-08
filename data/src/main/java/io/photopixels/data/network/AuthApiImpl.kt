package io.photopixels.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.photopixels.data.base.request
import io.photopixels.data.network.requests.LoginRequest
import io.photopixels.data.network.responses.LoginResponse
import io.photopixels.data.network.responses.RefreshTokenRequest
import io.photopixels.domain.base.Response

internal class AuthApiImpl(
    private val httpClient: HttpClient
) : AuthApi {

    override suspend fun loginUser(email: String, password: String): Response<LoginResponse> =
        request {
            val result = httpClient
                .post {
                    url("/api/user/login")
                    setBody(LoginRequest(email, password))
                }.body<LoginResponse>()
            Response.Success(result)
        }

    override suspend fun refreshToken(refreshToken: String): Response<LoginResponse> =
        request {
            val result = httpClient
                .post {
                    url("/api/user/refresh")
                    setBody(RefreshTokenRequest(refreshToken))
                }.body<LoginResponse>()

            Response.Success(result)
        }
}
