package io.photopixels.domain.repository

interface GooglePhotosRepository {
    suspend fun fetchGooglePhotos()
}
