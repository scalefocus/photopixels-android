package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.utils.Hasher
import javax.inject.Inject

class UploadGooglePhotoUseCase @Inject constructor(
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val downloadThumbnailUseCase: DownloadThumbnailUseCase,
) {

    suspend fun invoke(googlePhotoData: GooglePhoto, googlePhotoBytes: ByteArray): Response<PhotoUploadData> {
        val result = uploadPhotoUseCase.invoke(
            fileBytes = googlePhotoBytes,
            fileName = googlePhotoData.fileName,
            mimeType = googlePhotoData.mimeType,
            androidCloudId = googlePhotoData.androidCloudId,
            objectHash = Hasher.sha1HashBase64(googlePhotoBytes),
        )

        if (result is Response.Success) {
            downloadThumbnailUseCase.invoke(result.result.id)
        }

        return result
    }
}
