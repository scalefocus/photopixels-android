package io.photopixels.domain.usecases

import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.repository.GooglePhotosRepository
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class UpdatePhotoInDbUseCase @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun invoke(deviceMedia: DeviceMedia) {
        photosRepository.updatePhotoData(deviceMedia)
    }

    suspend fun updatedGooglePhotoData(googlePhoto: GooglePhoto) {
        googlePhotosRepository.updatePhotoData(googlePhoto)
    }
}
