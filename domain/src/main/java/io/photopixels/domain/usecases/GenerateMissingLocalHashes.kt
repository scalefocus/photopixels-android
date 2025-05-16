package io.photopixels.domain.usecases

import android.content.Context
import android.net.Uri
import io.photopixels.domain.extensions.readFileContent
import io.photopixels.domain.repository.PhotosRepository
import io.photopixels.domain.utils.Hasher
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class GenerateMissingLocalHashes @Inject constructor(
    private val photosRepository: PhotosRepository
) {

    suspend operator fun invoke(context: Context) = supervisorScope {
        val contentResolver = context.contentResolver

        photosRepository.getPhotosWithMissingHashes().map { photo ->
            runCatching {
                contentResolver.readFileContent(Uri.parse(photo.contentUri))?.let { photoBytes ->
                    val hash = Hasher.sha1HashBase64(photoBytes)
                    val photoUi = photosRepository.getPhotoByHash(hash)

                    val updatedPhoto = if (photoUi != null) {
                        photo.copy(hash = hash, isAlreadyUploaded = true, serverItemHashId = photoUi.id)
                    } else {
                        photo.copy(hash = hash)
                    }
                    photosRepository.updatePhotoData(updatedPhoto)
                }
            }
        }
    }
}
