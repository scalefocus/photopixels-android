package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class DownloadThumbnailUseCase @Inject constructor(private val photosRepository: PhotosRepository) {

    suspend operator fun invoke(thumbnailId: String): Response<List<ServerMedia>> {
        val response = photosRepository.getServerThumbnails(listOf(thumbnailId))
        if (response is Response.Success) {
            photosRepository.insertThumbnailsToDb(
                response.result.map {
                    it.copy(isNewlyUploaded = true)
                }
            )
        }
        return response
    }
}
