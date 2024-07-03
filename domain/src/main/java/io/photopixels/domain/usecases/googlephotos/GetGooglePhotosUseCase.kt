package io.photopixels.domain.usecases.googlephotos

import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GetGooglePhotosUseCase @Inject constructor(
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun invoke(): PhotoPixelError? = googlePhotosRepository.fetchGooglePhotos()
}
