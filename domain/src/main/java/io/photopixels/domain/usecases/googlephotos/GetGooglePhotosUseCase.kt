package io.photopixels.domain.usecases.googlephotos

import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoPickingSession
import io.photopixels.domain.repository.GooglePhotosRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GetGooglePhotosUseCase @Inject constructor(
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun invoke(sessionId: String, initialPollInterval: String): PhotoPixelError? {
        val pickedSession = pollSession(sessionId, initialPollInterval)

        return if (pickedSession == null) {
            PhotoPixelError.GenericGoogleError
        } else {
            downloadPickedPhotos(pickedSession)
        }
    }

    private suspend fun downloadPickedPhotos(pickedSession: PhotoPickingSession): PhotoPixelError? {
        var nextPageToken: String? = null

        do {
            val response = googlePhotosRepository.getMediaItems(pickedSession.id)
            if (response is Response.Success) {
                nextPageToken = response.result.nextPageToken
                googlePhotosRepository.insertPhotosToDb(response.result.mediaItems)
            } else if (response is Response.Failure) {
                return response.error
            }
        } while (nextPageToken != null)

        return null
    }

    private suspend fun pollSession(sessionId: String, pollInterval: String): PhotoPickingSession? {
        val pollingDelay = parsePollInterval(pollInterval)
        delay(pollingDelay)

        val result = googlePhotosRepository.updateGooglePhotoPickingSession(sessionId)
        return if (result is Response.Success) {
            val updateSession = result.result
            if (updateSession.mediaItemsSet) {
                // media is picked
                updateSession
            } else {
                pollSession(updateSession.id, updateSession.pollInterval)
            }
        } else {
            null
        }
    }

    private fun parsePollInterval(pollInterval: String) =
        Duration.parseOrNull(pollInterval) ?: DEFAULT_POLL_INTERVAL.toDuration(DurationUnit.SECONDS)

    companion object {
        private const val DEFAULT_POLL_INTERVAL = 5
    }
}
