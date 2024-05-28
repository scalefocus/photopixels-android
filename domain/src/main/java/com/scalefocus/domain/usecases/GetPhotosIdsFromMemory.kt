package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class GetPhotosIdsFromMemory @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke() = photosRepository.getAllPreviewPhotosIdsFromMemory()
}
