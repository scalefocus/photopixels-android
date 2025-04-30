package io.photopixels.domain.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScanDevicePhotosUseCase @Inject constructor(
    private val updatePhotosUseCase: UpdatePhotosDataToDbUseCase,
    private val getDevicePhotosUseCase: GetDevicePhotosUseCase,
    private val generateMissingLocalHashes: GenerateMissingLocalHashes,
    @ApplicationContext private val context: Context,
) {

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val photos = getDevicePhotosUseCase.invoke(context)
        updatePhotosUseCase.invoke(photos)
        generateMissingLocalHashes(context)
    }
}
