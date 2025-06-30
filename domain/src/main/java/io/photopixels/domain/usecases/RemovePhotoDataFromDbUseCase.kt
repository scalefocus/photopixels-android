package io.photopixels.domain.usecases

import io.photopixels.domain.repository.DeviceMediaRepository
import javax.inject.Inject

class RemovePhotoDataFromDbUseCase @Inject constructor(private val deviceMediaRepository: DeviceMediaRepository) {

    suspend fun invoke(photoId: Int) = deviceMediaRepository.removeMediaDataFromDB(photoId)

    suspend fun invoke(photoIds: List<Int>) = deviceMediaRepository.removeMediaDataFromDB(photoIds)
}
