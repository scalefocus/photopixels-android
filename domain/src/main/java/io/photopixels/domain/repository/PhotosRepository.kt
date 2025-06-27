package io.photopixels.domain.repository

import android.content.Context
import android.net.Uri
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.Thumbnail
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {
    suspend fun insertPhotoDataToDB(deviceMediaList: List<DeviceMedia>)

    suspend fun updatePhotoDataToDB(deviceMediaList: List<DeviceMedia>)

    fun getDeviceMedia(context: Context): List<DeviceMedia>

    suspend fun getDevicePhotosByHashes(hashes: List<String>): List<DeviceMedia>

    suspend fun getPhotoByHash(hash: String): PhotoUiData?

    suspend fun getPhotosWithMissingHashes(): List<DeviceMedia>

    suspend fun getPhotosDataForUploadFromDB(): List<DeviceMedia>

    suspend fun removePhotoDataFromDB(mediaId: Int)

    suspend fun removePhotosDataFromDB(mediaIds: List<Int>)

    suspend fun getPhotosDataIdsFromDB(): List<Int>

    suspend fun updatePhotoData(deviceMedia: DeviceMedia)

    suspend fun getServerThumbnails(serverItemHashIds: List<String>): Response<List<PhotoUiData>>

    suspend fun uploadPhoto(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData>

    suspend fun uploadPhoto(
        uri: Uri,
        fileName: String,
        objectHash: String,
    ): Flow<Double>

    suspend fun clearPhotosTable()

    suspend fun insertThumbnailsToDb(thumbnailsList: List<PhotoUiData>)

    fun getLocalThumbnailsFromDb(): Flow<List<Thumbnail.LocalThumbnail>>

    fun getRemoteThumbnailsFromDb(): Flow<List<Thumbnail.RemoteThumbnail>>

    suspend fun clearNewlyUploadedThumbnails()

    suspend fun getThumbnailsFromDbCount(): Int

    suspend fun deleteThumbnailsFromDb(ids: List<String>)

    suspend fun clearThumbnailsTable()

    suspend fun deletePhoto(photoServerId: String): Response<Unit>
}
