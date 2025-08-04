package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerAddress
import io.photopixels.domain.model.ServerStatus
import io.photopixels.domain.repository.ServerRepository
import javax.inject.Inject

class GetServerStatusUseCase @Inject constructor(
    private val serverRepository: ServerRepository
) {
    suspend fun invoke(
        serverAddress: ServerAddress
    ): Response<ServerStatus> = serverRepository.getServerStatus(serverAddress)
}
