package io.photopixels.domain.usecases

import android.content.Context
import androidx.core.net.toUri
import io.photopixels.domain.extensions.readFileContent
import io.photopixels.domain.repository.DeviceMediaRepository
import io.photopixels.domain.repository.ServerMediaRepository
import io.photopixels.domain.utils.Hasher
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class GenerateMissingLocalHashes @Inject constructor(
    private val deviceMediaRepository: DeviceMediaRepository,
    private val serverMediaRepository: ServerMediaRepository,
) {

    suspend operator fun invoke(context: Context) = supervisorScope {
        val contentResolver = context.contentResolver

        deviceMediaRepository.getMediaWithMissingHashes().map { photo ->
            runCatching {
                contentResolver.readFileContent(photo.contentUri.toUri())?.let { photoBytes ->
                    val hash = Hasher.sha1HashBase64(photoBytes)
                    val photoUi = serverMediaRepository.getMediaByHash(hash)

                    val updatedPhoto = if (photoUi != null) {
                        photo.copy(hash = hash, isAlreadyUploaded = true, serverItemHashId = photoUi.id)
                    } else {
                        photo.copy(hash = hash)
                    }
                    deviceMediaRepository.updateMediaData(updatedPhoto)
                }
            }
        }
    }
}
