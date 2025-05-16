package io.photopixels.domain.usecases

import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class UpdatePhotosDataToDbUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend fun invoke(photosDataList: List<PhotoData>) {
        removeDeletedDevicePhotos(photosDataList)

        photosRepository.insertPhotoDataToDB(photosDataList)
    }

    private suspend fun removeDeletedDevicePhotos(photosDataList: List<PhotoData>) {
        val idsFromDb = photosRepository.getPhotosDataIdsFromDB()

        val idsFromMediaStore = photosDataList.map { it.id.toInt() }.toSet()

        (idsFromDb - idsFromMediaStore).takeIf { it.isNotEmpty() }?.let { idsToRemove ->
            photosRepository.removePhotosDataFromDB(idsToRemove)
        }
    }
}
