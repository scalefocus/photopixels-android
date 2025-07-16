package io.photopixels.domain.usecases

import io.photopixels.domain.repository.DeviceMediaRepository
import javax.inject.Inject

class RemoveDeviceMediaDataFromDbUseCase @Inject constructor(private val deviceMediaRepository: DeviceMediaRepository) {

    suspend fun invoke(photoId: Int) = deviceMediaRepository.removeMediaDataFromDb(photoId)

    suspend fun invoke(photoIds: List<Int>) = deviceMediaRepository.removeMediaDataFromDb(photoIds)
}
