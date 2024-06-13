package io.photopixels.domain.usecases

import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class SaveThumbnailsToDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(thumbnailsList: List<PhotoUiData>) {
        photosRepository.insertThumbnailsToDb(thumbnailsList)
    }
}
