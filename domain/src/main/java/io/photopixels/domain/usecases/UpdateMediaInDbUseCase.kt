package io.photopixels.domain.usecases

import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.repository.DeviceMediaRepository
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class UpdateMediaInDbUseCase @Inject constructor(
    private val deviceMediaRepository: DeviceMediaRepository,
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun invoke(deviceMedia: DeviceMedia) {
        deviceMediaRepository.updateMediaData(deviceMedia)
    }

    suspend fun updatedGooglePhotoData(googlePhoto: GooglePhoto) {
        googlePhotosRepository.updatePhotoData(googlePhoto)
    }
}
