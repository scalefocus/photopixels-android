package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerAddress
import io.photopixels.domain.model.ServerStatus
import io.photopixels.domain.repository.ServerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServerStatusUseCase @Inject constructor(
    private val serverRepository: ServerRepository
) {
    suspend fun invoke(
        serverAddress: ServerAddress
    ): Flow<Response<ServerStatus>> = serverRepository.getServerStatus(serverAddress)
}
