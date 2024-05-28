package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.ServerRepository
import javax.inject.Inject

class GetServerInfoUseCase @Inject constructor(private val serverRepository: ServerRepository) {
    suspend fun getServerAddress() = serverRepository.getServerAddress()

    suspend fun getServerVersion() = serverRepository.getServerVersion()
}
