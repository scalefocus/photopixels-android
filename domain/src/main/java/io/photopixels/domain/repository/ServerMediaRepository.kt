package io.photopixels.domain.repository

import android.net.Uri
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.model.Thumbnail
import kotlinx.coroutines.flow.Flow

interface ServerMediaRepository {

    suspend fun getMediaByHash(hash: String): ServerMedia?

    suspend fun getServerThumbnails(serverItemHashIds: List<String>): Response<List<ServerMedia>>

    suspend fun uploadMedia(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData>

    suspend fun uploadMedia(
        uri: Uri,
        fileName: String,
        objectHash: String,
    ): Flow<Double>

    suspend fun insertThumbnailsToDb(thumbnailsList: List<ServerMedia>)

    fun getRemoteThumbnailsFromDb(): Flow<List<Thumbnail.RemoteThumbnail>>

    suspend fun clearNewlyUploadedThumbnails()

    suspend fun getThumbnailsFromDbCount(): Int

    suspend fun deleteThumbnailsFromDb(ids: List<String>)

    suspend fun clearThumbnailsTable()

    suspend fun deleteMedia(mediaServerId: String): Response<Unit>
}
