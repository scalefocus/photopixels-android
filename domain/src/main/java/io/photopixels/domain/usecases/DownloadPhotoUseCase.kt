package io.photopixels.domain.usecases

import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class DownloadPhotoUseCase @Inject constructor(
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun downloadGooglePhoto(photoUrl: String) = googlePhotosRepository.downloadPhoto(photoUrl)
}
