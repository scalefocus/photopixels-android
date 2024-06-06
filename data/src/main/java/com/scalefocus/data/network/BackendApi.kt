package com.scalefocus.data.network

import com.scalefocus.data.network.responses.LoginResponse
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.model.PhotoUploadData
import com.scalefocus.domain.model.ServerRevision
import com.scalefocus.domain.model.ServerStatus

interface BackendApi {
    companion object {
        private const val DEFAULT_REVISION_NUMBER = 0
    }

    suspend fun getServerStatus(serverAddress: String): Response<ServerStatus>

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
}
