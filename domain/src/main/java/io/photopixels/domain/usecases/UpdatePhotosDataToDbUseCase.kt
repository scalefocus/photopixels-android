package io.photopixels.domain.usecases

import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.repository.DeviceMediaRepository
import javax.inject.Inject

class UpdatePhotosDataToDbUseCase @Inject constructor(private val deviceMediaRepository: DeviceMediaRepository) {
    suspend fun invoke(photosDataList: List<DeviceMedia>) {
        removeDeletedDevicePhotos(photosDataList)

        deviceMediaRepository.insertMediaDataToDb(photosDataList)
    }

    private suspend fun removeDeletedDevicePhotos(photosDataList: List<DeviceMedia>) {
        val idsFromDb = deviceMediaRepository.getMediaDataIdsFromDb()

        val idsFromMediaStore = photosDataList.map { it.id.toInt() }.toSet()

        (idsFromDb - idsFromMediaStore).takeIf { it.isNotEmpty() }?.let { idsToRemove ->
            deviceMediaRepository.removeMediaDataFromDb(idsToRemove)
        }
    }
}
