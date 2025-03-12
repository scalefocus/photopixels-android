package io.photopixels.data.repository

import io.photopixels.data.managers.GooglePhotosManager
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.network.BackendApi
import io.photopixels.data.storage.database.GooglePhotosDao
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.model.PhotoPickingSession
import io.photopixels.domain.repository.AuthRepository
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GooglePhotosRepositoryImpl @Inject constructor(
    private val googlePhotosManager: GooglePhotosManager,
    private val googlePhotosDao: GooglePhotosDao,
    private val backendApi: BackendApi,
    private val authRepository: AuthRepository,
) : GooglePhotosRepository {
    override suspend fun fetchGooglePhotos(): PhotoPixelError? = googlePhotosManager.fetchGooglePhotos()

    override suspend fun getPhotosForUpload(): List<GooglePhoto> = googlePhotosDao.getPhotosForUpload().map {
        it.toDomain()
    }

    override suspend fun updatePhotoData(googlePhoto: GooglePhoto) {
        googlePhotosDao.updatePhotoData(googlePhoto.toEntity())
    }

    override suspend fun downloadPhoto(photoUrl: String): Response<ByteArray> = backendApi.downloadPhoto(photoUrl)

    override suspend fun clearGooglePhotosTable() {
        googlePhotosDao.clearTable()
    }

    override suspend fun createGooglePhotoPickingSession(): Response<PhotoPickingSession> {
        return authRepository.getGoogleAuthToken()?.let {
            backendApi.createGooglePhotoPickingSession(it)
        } ?: Response.Failure(PhotoPixelError.GenericGoogleError)
    }

    override suspend fun getGooglePhotoPickingSession(sessionId: String): Response<PhotoPickingSession> {
        return authRepository.getGoogleAuthToken()?.let {
            backendApi.getGooglePhotoPickingSession(it, sessionId)
        } ?: Response.Failure(PhotoPixelError.GenericGoogleError)
    }
}
