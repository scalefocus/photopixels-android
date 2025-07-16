package io.photopixels.domain.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScanDevicePhotosUseCase @Inject constructor(
    private val updatePhotosUseCase: UpdateDeviceMediaDataToDbUseCase,
    private val getDeviceMediaUseCase: GetDeviceMediaUseCase,
    private val generateMissingLocalHashes: GenerateMissingLocalHashes,
    @ApplicationContext private val context: Context,
) {

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val photos = getDeviceMediaUseCase.invoke(context)
        updatePhotosUseCase.invoke(photos)
        generateMissingLocalHashes(context)
    }
}
