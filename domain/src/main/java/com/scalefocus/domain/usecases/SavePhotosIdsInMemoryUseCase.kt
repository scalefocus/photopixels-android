package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class SavePhotosIdsInMemoryUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(photosIdsList: List<String>) {
        photosRepository.setAllPreviewPhotosIdsInMemory(photosIdsList)
    }
}
