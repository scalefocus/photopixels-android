package com.scalefocus.data.repository

import com.scalefocus.data.managers.GooglePhotosManager
import com.scalefocus.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GooglePhotosRepositoryImpl @Inject constructor(
    private val googlePhotosManager: GooglePhotosManager
) : GooglePhotosRepository {
    override suspend fun fetchGooglePhotos() {
        googlePhotosManager.fetchGooglePhotos()
    }
}
