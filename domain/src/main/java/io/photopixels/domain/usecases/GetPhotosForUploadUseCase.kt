package io.photopixels.domain.usecases

import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class GetPhotosForUploadUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(): List<PhotoData> {
        return photosRepository.getPhotosDataForUploadFromDB()
    }
}
