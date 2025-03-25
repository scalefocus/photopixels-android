package io.photopixels.data.repository

import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.network.GooglePhotosApi
import io.photopixels.data.storage.database.GooglePhotosDao
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.model.MediaItems
import io.photopixels.domain.model.PhotoPickingSession
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GooglePhotosRepositoryImpl @Inject constructor(
    private val googlePhotosDao: GooglePhotosDao,
    private val googlePhotosApi: GooglePhotosApi,
) : GooglePhotosRepository {

    override suspend fun getPhotosForUpload(): List<GooglePhoto> = googlePhotosDao.getPhotosForUpload().map {
        it.toDomain()
    }

    override suspend fun insertPhotosToDb(googlePhotos: List<GooglePhoto>) {
        googlePhotosDao.insertPhotosData(googlePhotos.map { it.toEntity() })
    }

    override suspend fun updatePhotoData(googlePhoto: GooglePhoto) {
        googlePhotosDao.updatePhotoData(googlePhoto.toEntity())
    }

    override suspend fun downloadPhoto(photoUrl: String): Response<ByteArray> = googlePhotosApi.downloadPhoto(photoUrl)

    override suspend fun clearGooglePhotosTable() {
        googlePhotosDao.clearTable()
    }

    override suspend fun createGooglePhotoPickingSession(): Response<PhotoPickingSession> =
        googlePhotosApi.createGooglePhotoPickingSession()

    override suspend fun updateGooglePhotoPickingSession(sessionId: String): Response<PhotoPickingSession> =
        googlePhotosApi.updateGooglePhotoPickingSession(sessionId)

    override suspend fun deleteGooglePhotoPickingSession(sessionId: String): Response<Unit> =
        googlePhotosApi.deleteGooglePhotoPickingSession(sessionId)

    override suspend fun getMediaItems(sessionId: String, pageToken: String?): Response<MediaItems> =
        googlePhotosApi.getMediaItems(sessionId, pageToken)
}
