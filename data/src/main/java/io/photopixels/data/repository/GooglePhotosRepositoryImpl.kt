package io.photopixels.data.repository

import io.photopixels.data.managers.GooglePhotosManager
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GooglePhotosRepositoryImpl @Inject constructor(
    private val googlePhotosManager: GooglePhotosManager
) : GooglePhotosRepository {
    override suspend fun fetchGooglePhotos() {
        googlePhotosManager.fetchGooglePhotos()
    }
}
