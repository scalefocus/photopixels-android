package io.photopixels.domain.usecases

import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.repository.DeviceMediaRepository
import io.photopixels.domain.repository.ServerMediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetThumbnailsFromDbUseCase @Inject constructor(
    private val deviceMediaRepository: DeviceMediaRepository,
    private val serverMediaRepository: ServerMediaRepository,
) {
    operator fun invoke(): Flow<List<Thumbnail>> {
        val localThumbnailsFlow = deviceMediaRepository.getLocalThumbnailsFromDb()

        val remoteThumbnailsFlow = serverMediaRepository.getRemoteThumbnailsFromDb()

        return localThumbnailsFlow.combine(remoteThumbnailsFlow) { local, remote ->
            (local + remote).sortedByDescending { it.dateCreated }
        }
    }
}
