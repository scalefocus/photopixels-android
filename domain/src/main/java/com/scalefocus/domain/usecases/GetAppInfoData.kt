package com.scalefocus.domain.usecases

import com.scalefocus.domain.model.AppInfoData
import com.scalefocus.domain.repository.AuthRepository
import com.scalefocus.domain.repository.ServerRepository
import javax.inject.Inject

class GetAppInfoData @Inject constructor(
    private val serverRepository: ServerRepository,
    private val authRepository: AuthRepository
) {

    suspend fun invoke(appVersion: String): AppInfoData {
        val serverAddress = serverRepository.getServerAddress() ?: ""
        val loggedUser = authRepository.getUsername() ?: ""
        val serverVersion = serverRepository.getServerVersion()

        return AppInfoData(serverAddress, serverVersion, appVersion, loggedUser)
    }
}
