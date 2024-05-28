package com.scalefocus.domain.usecases

import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class RemovePhotoDataFromDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {

    suspend fun invoke(photoId: Int) = photosRepository.removePhotoDataFromDB(photoId)
}
