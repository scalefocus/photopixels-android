package io.photopixels.data.network.tus

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.photopixels.data.network.AuthApi
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.data.storage.datastore.UserPreferencesDataStore
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.exceptions.ResumableUploadException
import io.photopixels.domain.model.ServerAddress
import io.tus.android.client.TusPreferencesURLStore
import io.tus.java.client.ProtocolException
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

// Upload the file in chunks of 8KB sizes.
private const val UPLOAD_CHUNK_SIZE = 8 * 1024
private const val MAX_PROGRESS = 100

/**
 * Tus client wrapper that handles tus client setup with correct URL,
 * file upload, token refresh and error handling.
 */
class ResumableUploadClient @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val userDataStore: UserPreferencesDataStore,
    private val authApi: AuthApi,
    @ApplicationContext private val context: Context,
) {

    private val tusClient: TusClient by lazy {
        val prefs = context.getSharedPreferences("tus_preferences", Context.MODE_PRIVATE)
        TusClient().apply {
            enableResuming(TusPreferencesURLStore(prefs))
        }
    }

    suspend fun uploadFile(upload: TusUpload): Flow<Double> = withContext(Dispatchers.IO) {
        try {
            uploadFileInt(upload)
        } catch (e: ProtocolException) {
            when (e.causingConnection?.responseCode) {
                HttpStatusCode.Unauthorized.value -> {
                    // token is expired, refresh it and try again
                    refreshToken()
                    uploadFileInt(upload)
                }

                HttpStatusCode.Conflict.value -> throw ResumableUploadException(PhotoPixelError.DuplicatePhotoError, e)

                HttpStatusCode.BadRequest.value -> throw ResumableUploadException(PhotoPixelError.GenericError, e)

                else -> throw ResumableUploadException(PhotoPixelError.ServerError, e)
            }
        }
    }

    private fun uploadFileInt(upload: TusUpload): Flow<Double> = flow {
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
            emit(progress)
        } while (uploader.uploadChunk() > -1)

        // Allow the HTTP connection to be closed and cleaned up
        uploader.finish()
    }

    private suspend fun refreshToken() {
        authDataStore.getRefreshToken()?.let {
            val response = authApi.refreshToken(it)
            if (response is Response.Success) {
                authDataStore.storeAuthHeaders(response.result.accessToken, response.result.refreshToken)
            }
        }
    }

    private suspend fun getTusClient(): TusClient {
        with(tusClient) {
            // set upload creation URL
            uploadCreationURL = userDataStore.getServerAddress()?.toUploadCreationUrl()

            // set auth token
            headers = authDataStore.getAuthToken()?.let { mapOf(HttpHeaders.Authorization to "Bearer $it") }
        }

        return tusClient
    }

    private fun ServerAddress.toUploadCreationUrl() = URL(
        protocol,
        host,
        port,
        "api/create_upload"
    )
}
