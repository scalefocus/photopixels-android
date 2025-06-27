package io.photopixels.domain.usecases

import android.content.Context
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class GetDeviceMediaUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    fun invoke(context: Context): List<DeviceMedia> {
        return photosRepository.getDeviceMedia(context)
    }
}
