package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.AuthRepository
import com.scalefocus.domain.repository.PhotosRepository
import com.scalefocus.domain.repository.ServerRepository
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
        }
    }
}
