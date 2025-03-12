package io.photopixels.domain.usecases.googlephotos

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoPickingSession
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GetGooglePhotosPickingSessionUseCase @Inject constructor(
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend operator fun invoke(sessionId: String? = null): Response<PhotoPickingSession> = if (sessionId == null) {
        googlePhotosRepository.createGooglePhotoPickingSession()
    } else {
        googlePhotosRepository.getGooglePhotoPickingSession(sessionId)
    }
}
