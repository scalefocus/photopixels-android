package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.repository.PhotosRepository
import io.photopixels.domain.repository.ServerRepository
import javax.inject.Inject

private const val MAX_OBJECTS_TO_REQUEST = 90

class SyncServerThumbnails @Inject constructor(
    private val serverRepository: ServerRepository,
    private val photosRepository: PhotosRepository,
) {

    suspend operator fun invoke(isUploadComplete: Boolean): Response<Unit> {
        photosRepository.clearNewlyUploadedThumbnails()

        val localRevision = serverRepository.getLocalRevision()
        val revisionToRequest = localRevision + 1

        return when (val revisionResponse = serverRepository.getServerRevision(revisionToRequest)) {
            is Response.Success -> {
                if (revisionResponse.result.version > localRevision) {
                    updateThumbnails(revisionResponse.result, isUploadComplete)
                } else {
                    Response.Success(Unit)
                }
            }

            is Response.Failure -> revisionResponse
        }
    }

    private suspend fun updateThumbnails(
        serverRevision: ServerRevision,
        isUploadComplete: Boolean
    ): Response<Unit> {
        serverRevision.deleted
            ?.takeIf { it.isNotEmpty() }
            ?.let { deletedIds -> photosRepository.deleteThumbnailsFromDb(deletedIds) }

        val response = getServerThumbnailsChunked(
            added = serverRevision.added,
            isUploadComplete = isUploadComplete
        )
        if (response is Response.Success) {
            serverRepository.setLocalRevision(serverRevision.version)
        }

        return response
    }

    /**
     * Get PhotoPixels thumbnails in chunks(multiple requests) if there are more than @MAX_OBJECTS_TO_REQUEST
     * Note: At this moment the server threshold is MAX 100 items per request
     */
    private suspend fun getServerThumbnailsChunked(
        added: Map<String, Long>,
        isUploadComplete: Boolean
    ): Response<Unit> {
        added.entries.sortedByDescending { (_, date) -> date }
            .chunked(MAX_OBJECTS_TO_REQUEST)
            .forEach {
                val serverIdsToFetch = it.map { (id, _) -> id }
                val thumbnailsResponse = photosRepository.getServerThumbnails(serverIdsToFetch)
                when (thumbnailsResponse) {
                    is Response.Success -> {
                        val thumbnails = thumbnailsResponse.result.map { photoUiData ->
                            photoUiData.copy(
                                dateTaken = added[photoUiData.id] ?: 0L,
                                isNewlyUploaded = isUploadComplete,
                            )
                        }
                        photosRepository.insertThumbnailsToDb(thumbnails)
                    }

                    // stop sync and return the failure
                    is Response.Failure -> return thumbnailsResponse
                }
            }

        return Response.Success(Unit)
    }
}
