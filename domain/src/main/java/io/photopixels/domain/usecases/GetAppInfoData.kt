package io.photopixels.domain.usecases

import io.photopixels.domain.model.AppInfoData
import io.photopixels.domain.model.ServerAddress
import io.photopixels.domain.repository.AuthRepository
import io.photopixels.domain.repository.ServerRepository
import javax.inject.Inject

class GetAppInfoData @Inject constructor(
    private val serverRepository: ServerRepository,
    private val authRepository: AuthRepository
) {

    suspend fun invoke(appVersion: String): AppInfoData {
        val serverAddress = serverRepository.getServerAddress() ?: ServerAddress()
        val loggedUser = authRepository.getUsername() ?: ""
        val serverVersion = serverRepository.getServerVersion()

        return AppInfoData(serverAddress, serverVersion, appVersion, loggedUser)
    }
}
