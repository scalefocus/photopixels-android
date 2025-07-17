package io.photopixels.domain.usecases

import io.photopixels.domain.repository.ServerMediaRepository
import javax.inject.Inject

class DeleteMediaUseCase @Inject constructor(
    private val serverMediaRepository: ServerMediaRepository
) {
    suspend fun invoke(photoServerId: String) = serverMediaRepository.deleteMedia(photoServerId)
}
