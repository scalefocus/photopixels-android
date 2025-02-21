package io.photopixels.domain.usecases

import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThumbnailsFromDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    operator fun invoke(): Flow<List<PhotoUiData>> {
        return photosRepository.getThumbnailsFromDb()
    }

    suspend fun getThumbnailsCount(): Int {
        return photosRepository.getThumbnailsFromDbCount()
    }
}
