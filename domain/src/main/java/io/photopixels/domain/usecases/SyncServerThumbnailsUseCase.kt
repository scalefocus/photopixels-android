package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.repository.DeviceMediaRepository
import io.photopixels.domain.repository.ServerMediaRepository
import io.photopixels.domain.repository.ServerRepository
import javax.inject.Inject

private const val MAX_OBJECTS_TO_REQUEST = 90

/**
 * Syncs to latest revision and download recently added server thumbnails to the local DB
 */
class SyncServerThumbnailsUseCase @Inject constructor(
    private val serverRepository: ServerRepository,
    private val serverMediaRepository: ServerMediaRepository,
    private val deviceMediaRepository: DeviceMediaRepository,
) {

    suspend operator fun invoke(hasNewlyUploadedMedia: Boolean): Response<Unit> {
        // Clear newly uploaded thumbnails if sync is executed manually or when app starts
        if (!hasNewlyUploadedMedia) serverMediaRepository.clearNewlyUploadedThumbnails()

        val localRevision = serverRepository.getLocalRevision()
        val revisionToRequest = if (localRevision == 0) 0 else localRevision + 1

        return when (val revisionResponse = serverRepository.getServerRevision(revisionToRequest)) {
            is Response.Success -> {
                if (revisionResponse.result.version > localRevision) {
                    updateThumbnails(revisionResponse.result, hasNewlyUploadedMedia)
                } else {
                    Response.Success(Unit)
                }
            }

            is Response.Failure -> revisionResponse
        }
    }

    private suspend fun updateThumbnails(
        serverRevision: ServerRevision,
        hasNewlyUploadedMedia: Boolean
    ): Response<Unit> {
        serverRevision.deleted
            ?.takeIf { it.isNotEmpty() }
            ?.let { deletedIds -> serverMediaRepository.deleteThumbnailsFromDb(deletedIds) }

        val response = getServerThumbnailsChunked(
            added = serverRevision.added,
            hasNewlyUploadedMedia = hasNewlyUploadedMedia
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
        hasNewlyUploadedMedia: Boolean
    ): Response<Unit> {
        added.entries.sortedByDescending { (_, date) -> date }
            .chunked(MAX_OBJECTS_TO_REQUEST)
            .forEach {
                val serverIdsToFetch = it.map { (id, _) -> id }
                val thumbnailsResponse = serverMediaRepository.getServerThumbnails(serverIdsToFetch)
                when (thumbnailsResponse) {
                    is Response.Success -> {
                        val thumbnails = thumbnailsResponse.result.map { photoUiData ->
                            photoUiData.copy(
                                isNewlyUploaded = hasNewlyUploadedMedia,
                            )
                        }
                        serverMediaRepository.insertThumbnailsToDb(thumbnails)

                        updateAlreadyUploadedDevicePhotos(thumbnails)
                    }

                    // stop sync and return the failure
                    is Response.Failure -> return thumbnailsResponse
                }
            }

        return Response.Success(Unit)
    }

    private suspend fun updateAlreadyUploadedDevicePhotos(thumbnails: List<ServerMedia>) {
        val hashToIdsMap = thumbnails.associate { thumbnail -> thumbnail.hash to thumbnail.id }
        deviceMediaRepository.getDeviceMediaByHashes(hashToIdsMap.keys.toList())
            .takeIf { it.isNotEmpty() }
            ?.map { photoData ->
                photoData.copy(
                    isAlreadyUploaded = true,
                    serverItemHashId = hashToIdsMap[photoData.hash]
                )
            }?.let { deviceMediaRepository.updateMediaDataToDb(it) }
    }
}
