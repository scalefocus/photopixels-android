package io.photopixels.domain.usecases

import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class GetPhotosIdsFromMemory @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke() = photosRepository.getAllPreviewPhotosIdsFromMemory()
}
