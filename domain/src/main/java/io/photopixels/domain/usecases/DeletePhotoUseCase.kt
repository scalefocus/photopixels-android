package io.photopixels.domain.usecases

import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class DeletePhotoUseCase @Inject constructor(
    private val photosRepository: PhotosRepository
) {
    suspend fun invoke(photoServerId: String) = photosRepository.deletePhoto(photoServerId)
}
