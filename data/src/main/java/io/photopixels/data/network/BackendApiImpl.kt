package io.photopixels.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.URLProtocol
import io.ktor.util.toByteArray
import io.photopixels.data.base.request
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.network.requests.ForgotPasswordRequest
import io.photopixels.data.network.requests.LoginRequest
import io.photopixels.data.network.requests.ObjectsDataRequest
import io.photopixels.data.network.requests.RegisterRequest
import io.photopixels.data.network.requests.ResetPasswordRequest
import io.photopixels.data.network.requests.UploadPhotoRequest
import io.photopixels.data.network.responses.LoginResponse
import io.photopixels.data.network.responses.ObjectResponse
import io.photopixels.data.network.responses.ObjectUploadResponse
import io.photopixels.data.network.responses.RefreshTokenRequest
import io.photopixels.data.network.responses.ServerRevisionResponse
import io.photopixels.data.network.responses.ServerStatusResponse
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.ServerAddress
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.model.ServerStatus
import javax.inject.Inject

class BackendApiImpl @Inject constructor(
    private val httpClient: HttpClient
) : BackendApi {

    override suspend fun getServerStatus(serverAddress: ServerAddress): Response<ServerStatus> =
        request {
            val result = httpClient
                .get {
                    url {
                        host = serverAddress.host
                        encodedPathSegments = listOf("api", "status")
                        protocol = URLProtocol.createOrDefault(serverAddress.protocol)
                        port = serverAddress.port
                    }
                }.body<ServerStatusResponse>()

            Response.Success(result.toDomain())
        }

    override suspend fun loginUser(email: String, password: String): Response<LoginResponse> =
        request {
            val result = httpClient
                .post {
                    url("/api/user/login")
                    setBody(LoginRequest(email, password))
                }.body<LoginResponse>()

            Response.Success(result)
        }

    override suspend fun clearBearerTokens() {
        httpClient
            .plugin(Auth)
            .providers
            .filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken()
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

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Response<Unit> = request {
        val result = httpClient
            .post {
                url("/api/user/register")
                setBody(RegisterRequest(name, email, password))
            }.body<Unit>()

        Response.Success(result)
    }

    override suspend fun getServerRevision(revisionNumber: Int?): Response<ServerRevision> =
        request {
            val result = httpClient
                .get {
                    url("/api/revision/$revisionNumber")
                }.body<ServerRevisionResponse>()
            Response.Success(result.toDomain())
        }

    override suspend fun getThumbnailsByIds(serverItemHashIds: List<String>): Response<List<PhotoUiData>> =
        request {
            val result = httpClient
                .post {
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
            val result = httpClient
                .post {
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
            httpClient
                .post {
                    url("/api/user/forgotpassword")
                    setBody(ForgotPasswordRequest(email))
                }.body<Unit>()

            Response.Success(Unit)
        }

    override suspend fun resetPassword(email: String, newPassword: String, verificationCode: String): Response<Unit> =
        request {
            httpClient
                .post {
                    url("/api/user/resetpassword")
                    setBody(ResetPasswordRequest(email, newPassword, verificationCode))
                }.body<Unit>()

            Response.Success(Unit)
        }

    override suspend fun downloadPhoto(photoUrl: String): Response<ByteArray> =
        request {
            val result = httpClient
                .get {
                    url(photoUrl)
                }.bodyAsChannel()
            Response.Success(result.toByteArray())
        }

    override suspend fun deletePhoto(photoServerId: String): Response<Unit> =
        request {
            httpClient
                .delete {
                    url("/api/object/$photoServerId")
                }.body<Unit>()
            Response.Success(Unit)
        }
}
