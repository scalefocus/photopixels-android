package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class GetServerThumbnailsUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend operator fun invoke(serverItemIds: List<String>): Response<List<PhotoUiData>> {
        return photosRepository.getServerThumbnails(serverItemIds)
    }
}
