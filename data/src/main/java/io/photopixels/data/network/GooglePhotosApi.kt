package io.photopixels.data.network

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.MediaItems
import io.photopixels.domain.model.PhotoPickingSession

interface GooglePhotosApi {

    suspend fun downloadPhoto(photoUrl: String): Response<ByteArray>

    suspend fun createGooglePhotoPickingSession(): Response<PhotoPickingSession>

    suspend fun updateGooglePhotoPickingSession(sessionId: String): Response<PhotoPickingSession>

    suspend fun deleteGooglePhotoPickingSession(sessionId: String): Response<Unit>

    suspend fun getMediaItems(sessionId: String, pageToken: String?): Response<MediaItems>
}
