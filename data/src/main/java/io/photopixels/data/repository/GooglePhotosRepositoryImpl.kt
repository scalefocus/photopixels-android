package io.photopixels.data.repository

import io.photopixels.data.managers.GooglePhotosManager
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.storage.database.GooglePhotosDao
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GooglePhotosRepositoryImpl @Inject constructor(
    private val googlePhotosManager: GooglePhotosManager,
    private val googlePhotosDao: GooglePhotosDao
) : GooglePhotosRepository {
    override suspend fun fetchGooglePhotos() {
        googlePhotosManager.fetchGooglePhotos()
    }

    override suspend fun getPhotosForUpload(): List<GooglePhoto> = googlePhotosDao.getPhotosForUpload().map {
        it.toDomain()
    }

    override suspend fun updatePhotoData(googlePhoto: GooglePhoto) {
        googlePhotosDao.updatePhotoData(googlePhoto.toEntity())
    }
}
