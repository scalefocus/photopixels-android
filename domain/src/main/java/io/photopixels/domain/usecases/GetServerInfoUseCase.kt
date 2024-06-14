package io.photopixels.domain.usecases

import io.photopixels.domain.repository.ServerRepository
import javax.inject.Inject

class GetServerInfoUseCase @Inject constructor(private val serverRepository: ServerRepository) {
    suspend fun getServerAddress() = serverRepository.getServerAddress()

    suspend fun getServerVersion() = serverRepository.getServerVersion()
}
