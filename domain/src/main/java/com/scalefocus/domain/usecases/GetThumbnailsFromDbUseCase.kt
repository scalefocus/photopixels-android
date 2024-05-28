package com.scalefocus.domain.usecases

import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class GetThumbnailsFromDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(): List<PhotoUiData> {
        return photosRepository.getThumbnailsFromDb()
    }
}
