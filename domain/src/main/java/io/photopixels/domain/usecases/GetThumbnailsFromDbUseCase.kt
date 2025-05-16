package io.photopixels.domain.usecases

import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetThumbnailsFromDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    operator fun invoke(): Flow<List<Thumbnail>> {
        val localThumbnailsFlow = photosRepository.getLocalThumbnailsFromDb()

        val remoteThumbnailsFlow = photosRepository.getRemoteThumbnailsFromDb()

        return localThumbnailsFlow.combine(remoteThumbnailsFlow) { local, remote ->
            (local + remote).sortedByDescending { it.dateCreated }
        }
    }

    suspend fun getThumbnailsCount(): Int {
        return photosRepository.getThumbnailsFromDbCount()
    }
}
