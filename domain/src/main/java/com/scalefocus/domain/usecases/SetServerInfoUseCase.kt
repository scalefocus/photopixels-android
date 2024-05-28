package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.ServerRepository
import javax.inject.Inject

class SetServerInfoUseCase @Inject constructor(private val serverRepository: ServerRepository) {
    suspend fun setServerAddress(serverAddress: String) {
        serverRepository.setServerAddressToDataStore(serverAddress)
    }

    suspend fun setServerVersion(serverVersion: String) {
        serverRepository.setServerVersionToDataStore(serverVersion)
    }
}
