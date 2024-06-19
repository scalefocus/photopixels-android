package io.photopixels.domain.usecases

import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class UpdatePhotoInDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(photoData: PhotoData) {
        photosRepository.updatePhotoData(photoData)
    }
}
