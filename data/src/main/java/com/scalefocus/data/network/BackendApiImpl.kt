package com.scalefocus.data.network

import com.scalefocus.data.base.request
import com.scalefocus.data.mappers.toDomain
import com.scalefocus.data.network.requests.ForgotPasswordRequest
import com.scalefocus.data.network.requests.LoginRequest
import com.scalefocus.data.network.requests.ObjectsDataRequest
import com.scalefocus.data.network.requests.RegisterRequest
import com.scalefocus.data.network.requests.ResetPasswordRequest
import com.scalefocus.data.network.requests.UploadPhotoRequest
import com.scalefocus.data.network.responses.LoginResponse
import com.scalefocus.data.network.responses.ObjectResponse
import com.scalefocus.data.network.responses.ObjectUploadResponse
import com.scalefocus.data.network.responses.RefreshTokenRequest
import com.scalefocus.data.network.responses.ServerRevisionResponse
import com.scalefocus.data.network.responses.ServerStatusResponse
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.model.PhotoUploadData
import com.scalefocus.domain.model.ServerRevision
import com.scalefocus.domain.model.ServerStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.URLProtocol
import javax.inject.Inject

class BackendApiImpl @Inject constructor(private val httpClient: HttpClient) : BackendApi {

    override suspend fun getServerStatus(serverAddress: String): Response<ServerStatus> =
        request {
            val result = httpClient.get {
                url {
                    host = serverAddress
                    encodedPathSegments = listOf("api", "status")
                    protocol = URLProtocol.HTTPS
                }
            }.body<ServerStatusResponse>()

            Response.Success(result.toDomain())
        }

    override suspend fun loginUser(email: String, password: String): Response<LoginResponse> =
        request {
            val result = httpClient.post {
                url("/api/user/login")
                setBody(LoginRequest(email, password))
            }.body<LoginResponse>()

            Response.Success(result)
        }

    override suspend fun refreshToken(refreshToken: String): Response<LoginResponse> =
        request {
            val result = httpClient.post {
                url("/api/user/refresh")
                setBody(RefreshTokenRequest(refreshToken))
            }.body<LoginResponse>()

            Response.Success(result)
        }

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Response<Unit> = request {
        val result = httpClient.post {
            url("/api/user/register")
            setBody(RegisterRequest(name, email, password))
        }.body<Unit>()

        Response.Success(result)
    }

    override suspend fun getServerRevision(revisionNumber: Int?): Response<ServerRevision> =
        request {
            val result = httpClient.get {
                url("/api/revision/$revisionNumber")
            }.body<ServerRevisionResponse>()
            Response.Success(result.toDomain())
        }

    override suspend fun getThumbnailsByIds(serverItemHashIds: List<String>): Response<List<PhotoUiData>> =
        request {
            val result = httpClient.post {
                url("/api/objects/data")
                setBody(ObjectsDataRequest(serverItemHashIds))
            }.body<List<ObjectResponse>>()

            Response.Success(result.map { it.toDomain() })
        }

    override suspend fun uploadPhoto(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData> =
        request {
            val result = httpClient.post {
                url("/api/object")
                setBody(
                    UploadPhotoRequest.build(
                        fileBytes = fileBytes,
                        fileName = fileName,
                        mimeType = mimeType,
                        androidCloudId = androidCloudId,
                        objectHash = objectHash
                    )
                )
            }.body<ObjectUploadResponse>()

            Response.Success(result.toDomain())
        }

    override suspend fun forgotPassword(email: String): Response<Unit> =
        request {
            httpClient.post {
                url("/api/user/forgotpassword")
                setBody(ForgotPasswordRequest(email))
            }.body<Unit>()

            Response.Success(Unit)
        }

    override suspend fun resetPassword(email: String, newPassword: String, verificationCode: String): Response<Unit> =
        request {
            httpClient.post {
                url("/api/user/resetpassword")
                setBody(ResetPasswordRequest(email, newPassword, verificationCode))
            }.body<Unit>()

            Response.Success(Unit)
        }
}
