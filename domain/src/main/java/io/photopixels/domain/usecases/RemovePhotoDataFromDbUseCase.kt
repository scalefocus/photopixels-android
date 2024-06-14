package io.photopixels.domain.usecases

import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class RemovePhotoDataFromDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {

    suspend fun invoke(photoId: Int) = photosRepository.removePhotoDataFromDB(photoId)
}
