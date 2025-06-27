package io.photopixels.domain.usecases

import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.repository.GooglePhotosRepository
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class GetPhotosForUploadUseCase @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun invoke(): List<DeviceMedia> = photosRepository.getPhotosDataForUploadFromDB()

    suspend fun getGooglePhotosFromDB() = googlePhotosRepository.getPhotosForUpload()
}
