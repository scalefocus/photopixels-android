package io.photopixels.domain.usecases

import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class SaveThumbnailsToDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(thumbnailsList: List<ServerMedia>) {
        photosRepository.insertThumbnailsToDb(thumbnailsList)
    }
}
