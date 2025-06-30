package io.photopixels.domain.usecases

import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.repository.ServerMediaRepository
import javax.inject.Inject

class SaveThumbnailsToDbUseCase @Inject constructor(private val serverMediaRepository: ServerMediaRepository) {
    suspend fun invoke(thumbnailsList: List<ServerMedia>) {
        serverMediaRepository.insertThumbnailsToDb(thumbnailsList)
    }
}
