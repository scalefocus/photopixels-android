package io.photopixels.data.network.tus

import android.content.Context
import android.database.CursorIndexOutOfBoundsException
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.FileNotFoundException
import javax.inject.Inject

private const val TAG = "ResumableUploadApi"

class ResumableUploadApi @Inject constructor(
    private val resumableUploadClient: ResumableUploadClient,
    @ApplicationContext private val context: Context,
) {

    suspend fun uploadFile(
        uri: Uri,
        fileName: String,
        objectHash: String,
    ): Flow<Double> {
        val upload = try {
            // create resumable upload from uri
            context.createResumableUpload(uri, fileName, objectHash)
        } catch (e: CursorIndexOutOfBoundsException) {
            Timber.tag(TAG).e(e, "Exception while opening file for upload")
            // file not found while creating upload
            throw FileNotFoundException()
        }

        // start file upload
        return resumableUploadClient.uploadFile(upload)
    }
}
