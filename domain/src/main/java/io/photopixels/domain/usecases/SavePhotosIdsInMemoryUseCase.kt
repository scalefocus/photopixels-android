package io.photopixels.domain.usecases

import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class SavePhotosIdsInMemoryUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(photosIdsList: List<String>) {
        photosRepository.setAllPreviewPhotosIdsInMemory(photosIdsList)
    }
}
