package io.photopixels.domain.usecases

import android.net.Uri
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.repository.ServerMediaRepository
import javax.inject.Inject

class UploadPhotoUseCase @Inject constructor(private val serverMediaRepository: ServerMediaRepository) {

    suspend fun invoke(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData> {
        return serverMediaRepository.uploadMedia(fileBytes, fileName, mimeType, androidCloudId, objectHash)
    }

    suspend fun invoke(
        uri: Uri,
        fileName: String,
        objectHash: String,
    ) = serverMediaRepository.uploadMedia(uri, fileName, objectHash)
}
