package com.scalefocus.domain.usecases

import com.scalefocus.domain.model.PhotoData
import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class SavePhotosDataToDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(photosDataList: List<PhotoData>) {
        photosRepository.insertPhotoDataToDB(photosDataList)
    }
}
