package io.photopixels.domain.repository

import io.photopixels.domain.model.GooglePhoto

interface GooglePhotosRepository {
    suspend fun fetchGooglePhotos()

    suspend fun getPhotosForUpload(): List<GooglePhoto>

    suspend fun updatePhotoData(googlePhoto: GooglePhoto)
}
