package io.photopixels.domain.usecases

import android.net.Uri
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.repository.PhotosRepository
import javax.inject.Inject

class UploadPhotoUseCase @Inject constructor(private val photosRepository: PhotosRepository) {

    suspend fun invoke(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData> {
        return photosRepository.uploadPhoto(fileBytes, fileName, mimeType, androidCloudId, objectHash)
    }

    suspend fun invoke(
        uri: Uri,
        fileName: String,
        objectHash: String,
    ) = photosRepository.uploadPhoto(uri, fileName, objectHash)
}
