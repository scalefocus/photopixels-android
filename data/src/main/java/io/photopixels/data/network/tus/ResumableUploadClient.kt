package io.photopixels.data.network.tus

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.http.HttpStatusCode
import io.photopixels.data.network.Authenticator
import io.photopixels.data.storage.datastore.UserPreferencesDataStore
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.exceptions.ResumableUploadException
import io.photopixels.domain.model.ServerAddress
import io.tus.android.client.TusPreferencesURLStore
import io.tus.java.client.ProtocolException
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.URL
import javax.inject.Inject

// Upload the file in chunks of 8KB sizes.
private const val UPLOAD_CHUNK_SIZE = 8 * 1024
private const val MAX_PROGRESS = 100

/**
 * Tus client wrapper that handles tus client setup with correct URL,
 * file upload, token refresh and error handling.
 */
internal class ResumableUploadClient @Inject constructor(
    private val authenticator: Authenticator,
    private val userDataStore: UserPreferencesDataStore,
    @ApplicationContext private val context: Context,
) {

    private val tusClient: TusClient by lazy {
        val prefs = context.getSharedPreferences("tus_preferences", Context.MODE_PRIVATE)
        TusClient().apply {
            enableResuming(TusPreferencesURLStore(prefs))
        }
    }

    fun uploadFile(upload: TusUpload): Flow<Double> = flow {
        try {
            uploadFileInternal(upload, progressListener = ::emit)
        } catch (e: ProtocolException) {
            when (e.causingConnection?.responseCode) {
                HttpStatusCode.Unauthorized.value -> {
                    // token is expired, refresh it and try again
                    authenticator.refreshToken()
                    // retry the upload
                    uploadFileInternal(upload, progressListener = ::emit)
                }

                HttpStatusCode.Conflict.value -> throw ResumableUploadException(PhotoPixelError.DuplicatePhotoError, e)

                HttpStatusCode.BadRequest.value -> throw ResumableUploadException(PhotoPixelError.GenericError, e)

                else -> throw ResumableUploadException(PhotoPixelError.ServerError, e)
            }
        } catch (e: IOException) {
            throw ResumableUploadException(PhotoPixelError.NoInternetConnection, e)
        }
    }

    private suspend fun uploadFileInternal(
        upload: TusUpload,
        progressListener: suspend (Double) -> Unit
    ) {
        // First try to resume an upload. If that's not possible we will create a new
        // upload and get a TusUploader in return. This class is responsible for opening
        // a connection to the remote server and doing the uploading.
        val uploader = getTusClient().resumeOrCreateUpload(upload)

        uploader.chunkSize = UPLOAD_CHUNK_SIZE

        // Upload the file as long as data is available. Once the
        // file has been fully uploaded the method will return -1
        do {
            // Calculate the progress using the total size of the uploading file and
            // the current offset.
            val totalBytes = upload.size
            val bytesUploaded = uploader.offset
            val progress = bytesUploaded.toDouble() / totalBytes * MAX_PROGRESS
            progressListener(progress)
        } while (uploader.uploadChunk() > -1)

        // Allow the HTTP connection to be closed and cleaned up
        uploader.finish()
    }

    private suspend fun getTusClient(): TusClient {
        with(tusClient) {
            // set upload creation URL
            uploadCreationURL = userDataStore.getServerAddress()?.toUploadCreationUrl()

            // set auth token
            headers = authenticator.getAuthHeader()?.let { mapOf(it) }
        }

        return tusClient
    }

    private fun ServerAddress.toUploadCreationUrl(): URL {
        val serverUrl = URL(toString())
        return URL(serverUrl, "api/create_upload")
    }
}
