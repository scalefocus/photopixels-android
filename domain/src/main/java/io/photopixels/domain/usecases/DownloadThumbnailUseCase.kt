package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.repository.ServerMediaRepository
import javax.inject.Inject

class DownloadThumbnailUseCase @Inject constructor(private val serverMediaRepository: ServerMediaRepository) {

    suspend operator fun invoke(thumbnailId: String): Response<List<ServerMedia>> {
        val response = serverMediaRepository.getServerThumbnails(listOf(thumbnailId))
        if (response is Response.Success) {
            serverMediaRepository.insertThumbnailsToDb(
                response.result.map {
                    it.copy(isNewlyUploaded = true)
                }
            )
        }
        return response
    }
}
