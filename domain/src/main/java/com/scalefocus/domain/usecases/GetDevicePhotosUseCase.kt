package com.scalefocus.domain.usecases

import android.content.Context
import com.scalefocus.domain.model.PhotoData
import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class GetDevicePhotosUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    fun invoke(context: Context): List<PhotoData> {
        return photosRepository.getDevicePhotos(context)
    }
}
