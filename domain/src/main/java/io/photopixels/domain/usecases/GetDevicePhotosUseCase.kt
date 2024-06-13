package io.photopixels.domain.usecases

import android.content.Context
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class GetDevicePhotosUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    fun invoke(context: Context): List<PhotoData> {
        return photosRepository.getDevicePhotos(context)
    }
}
