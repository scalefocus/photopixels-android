package io.photopixels.domain.usecases

import android.content.Context
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.repository.DeviceMediaRepository
import javax.inject.Inject

class GetDeviceMediaUseCase @Inject constructor(
    private val deviceMediaRepository: DeviceMediaRepository
) {
    fun invoke(context: Context): List<DeviceMedia> {
        return deviceMediaRepository.getDeviceMedia(context)
    }
}
