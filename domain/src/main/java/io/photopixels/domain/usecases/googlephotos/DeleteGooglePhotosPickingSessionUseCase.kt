package io.photopixels.domain.usecases.googlephotos

import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class DeleteGooglePhotosPickingSessionUseCase @Inject constructor(
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend operator fun invoke(sessionId: String) = googlePhotosRepository.deleteGooglePhotoPickingSession(sessionId)
}
