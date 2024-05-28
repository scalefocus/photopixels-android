package com.scalefocus.domain.usecases

import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class SaveThumbnailsToDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(thumbnailsList: List<PhotoUiData>) {
        photosRepository.insertThumbnailsToDb(thumbnailsList)
    }
}
