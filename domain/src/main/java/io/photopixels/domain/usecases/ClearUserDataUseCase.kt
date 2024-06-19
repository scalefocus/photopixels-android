package io.photopixels.domain.usecases

import io.photopixels.domain.repository.AuthRepository
import io.photopixels.domain.repository.PhotosRepository
import io.photopixels.domain.repository.ServerRepository
import javax.inject.Inject

class ClearUserDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val serverRepository: ServerRepository,
    private val photosRepository: PhotosRepository
) {
    suspend fun invoke(clearServerData: Boolean = true) {
        authRepository.clearUserData()
        photosRepository.clearPhotosTable()
        photosRepository.clearThumbnailsTable()

        if (clearServerData) {
            serverRepository.clearServerData()
            authRepository.clearBearerTokens()
        }
    }
}
