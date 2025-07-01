package io.photopixels.domain.usecases

import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.repository.DeviceMediaRepository
import io.photopixels.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GetPhotosForUploadUseCase @Inject constructor(
    private val deviceMediaRepository: DeviceMediaRepository,
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun invoke(): List<DeviceMedia> = deviceMediaRepository.getMediaDataForUploadFromDb()

    suspend fun getGooglePhotosFromDB() = googlePhotosRepository.getPhotosForUpload()
}
