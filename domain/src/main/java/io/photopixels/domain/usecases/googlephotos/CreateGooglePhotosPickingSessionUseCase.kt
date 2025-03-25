package io.photopixels.domain.usecases.googlephotos

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoPickingSession
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class CreateGooglePhotosPickingSessionUseCase @Inject constructor(
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend operator fun invoke(): Response<PhotoPickingSession> =
        googlePhotosRepository.createGooglePhotoPickingSession()
}
