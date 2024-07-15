package io.photopixels.data.network

import io.photopixels.data.network.responses.LoginResponse
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.ServerAddress
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.model.ServerStatus

interface BackendApi {
    companion object {
        private const val DEFAULT_REVISION_NUMBER = 0
    }

    suspend fun getServerStatus(serverAddress: ServerAddress): Response<ServerStatus>

    suspend fun loginUser(email: String, password: String): Response<LoginResponse>

    suspend fun clearBearerTokens()

    suspend fun refreshToken(refreshToken: String): Response<LoginResponse>

    suspend fun registerUser(name: String, email: String, password: String): Response<Unit>

    suspend fun getServerRevision(revisionNumber: Int? = DEFAULT_REVISION_NUMBER): Response<ServerRevision>

    suspend fun getThumbnailsByIds(serverItemHashIds: List<String>): Response<List<PhotoUiData>>

    suspend fun uploadPhoto(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData>

    suspend fun forgotPassword(email: String): Response<Unit>

    suspend fun resetPassword(email: String, newPassword: String, verificationCode: String): Response<Unit>

    suspend fun downloadPhoto(photoUrl: String): Response<ByteArray>
}
