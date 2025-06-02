package io.photopixels.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.toByteArray
import io.photopixels.data.base.request
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.network.responses.MediaItemsResponse
import io.photopixels.data.network.responses.PhotoPickingSessionResponse
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.MediaItems
import io.photopixels.domain.model.PhotoPickingSession

private const val SESSIONS_PATH = "sessions"

class GooglePhotosApiImpl(private val httpClient: HttpClient) : GooglePhotosApi {

    override suspend fun downloadPhoto(
        photoUrl: String
    ): Response<ByteArray> = request {
        val result = httpClient.get {
            url(photoUrl)
        }.bodyAsChannel()
        Response.Success(result.toByteArray())
    }

    override suspend fun createGooglePhotoPickingSession(): Response<PhotoPickingSession> = request {
        val result = httpClient.post {
            url(SESSIONS_PATH)
        }.body<PhotoPickingSessionResponse>()
        Response.Success(result.toDomain())
    }

    override suspend fun updateGooglePhotoPickingSession(
        sessionId: String
    ): Response<PhotoPickingSession> = request {
        val result = httpClient.get {
            url("${SESSIONS_PATH}/$sessionId")
        }.body<PhotoPickingSessionResponse>()
        Response.Success(result.toDomain())
    }

    override suspend fun deleteGooglePhotoPickingSession(
        sessionId: String
    ): Response<Unit> = request {
        val result = httpClient.delete {
            url("${SESSIONS_PATH}/$sessionId")
        }.body<Unit>()
        Response.Success(result)
    }

    override suspend fun getMediaItems(
        sessionId: String,
        pageToken: String?
    ): Response<MediaItems> = request {
        val result = httpClient.get {
            url("mediaItems")
            parameter("sessionId", sessionId)
            pageToken?.let { parameter("pageToken", pageToken) }
        }.body<MediaItemsResponse>()
        Response.Success(result.toDomain())
    }
}
