package io.photopixels.domain.usecases

import io.photopixels.domain.repository.ServerMediaRepository
import javax.inject.Inject

class RemoveThumbnailsFromDbUseCase @Inject constructor(private val serverMediaRepository: ServerMediaRepository) {
    suspend fun invoke() {
        serverMediaRepository.clearThumbnailsTable()
    }
}
