package io.photopixels.domain.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class GeneratePhotoThumbnail @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photosRepository: PhotosRepository,
) {
    suspend operator fun invoke(photo: PhotoData) {
        val thumbnail = photosRepository.loadPhotoThumbnail(context, photo)
        photosRepository.savePhotoThumbnailToDb(photo, thumbnail)
    }
}
