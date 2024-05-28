package com.scalefocus.domain.usecases

import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.PhotoUploadData
import com.scalefocus.domain.repository.PhotosRepository
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
}
