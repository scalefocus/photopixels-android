package io.photopixels.domain.usecases

import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class SavePhotosDataToDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(photosDataList: List<PhotoData>) {
        photosRepository.insertPhotoDataToDB(photosDataList)
    }
}
