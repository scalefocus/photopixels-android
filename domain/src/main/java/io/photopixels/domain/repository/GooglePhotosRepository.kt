package io.photopixels.domain.repository

import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.GooglePhoto

interface GooglePhotosRepository {
    suspend fun fetchGooglePhotos(): PhotoPixelError?

    suspend fun getPhotosForUpload(): List<GooglePhoto>

    suspend fun updatePhotoData(googlePhoto: GooglePhoto)

    suspend fun downloadPhoto(photoUrl: String): Response<ByteArray>

    suspend fun clearGooglePhotosTable()
}
