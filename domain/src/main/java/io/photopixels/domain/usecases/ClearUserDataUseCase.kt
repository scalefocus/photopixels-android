package io.photopixels.domain.usecases

import io.photopixels.domain.repository.AuthRepository
import io.photopixels.domain.repository.DeviceMediaRepository
import io.photopixels.domain.repository.GooglePhotosRepository
import io.photopixels.domain.repository.ServerMediaRepository
import io.photopixels.domain.repository.ServerRepository
import javax.inject.Inject

class ClearUserDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val serverRepository: ServerRepository,
    private val deviceMediaRepository: DeviceMediaRepository,
    private val serverMediaRepository: ServerMediaRepository,
    private val googlePhotosRepository: GooglePhotosRepository
) {
    suspend fun invoke(clearServerData: Boolean = true) {
        authRepository.clearUserData()
        deviceMediaRepository.clearMediaTable()
        serverMediaRepository.clearThumbnailsTable()
        googlePhotosRepository.clearGooglePhotosTable()
        authRepository.clearGoogleAuthState()
        serverRepository.clearLocalRevision()

        if (clearServerData) {
            serverRepository.clearServerData()
            authRepository.clearBearerTokens()
        }
    }
}
