package io.photopixels.domain.repository

import android.content.Context
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.Thumbnail
import kotlinx.coroutines.flow.Flow

interface DeviceMediaRepository {
    suspend fun insertMediaDataToDB(deviceMediaList: List<DeviceMedia>)

    suspend fun updateMediaDataToDB(deviceMediaList: List<DeviceMedia>)

    fun getDeviceMedia(context: Context): List<DeviceMedia>

    suspend fun getDeviceMediaByHashes(hashes: List<String>): List<DeviceMedia>

    suspend fun getMediaWithMissingHashes(): List<DeviceMedia>

    suspend fun getMediaDataForUploadFromDB(): List<DeviceMedia>

    suspend fun removeMediaDataFromDB(mediaId: Int)

    suspend fun removeMediaDataFromDB(mediaIds: List<Int>)

    suspend fun getMediaDataIdsFromDB(): List<Int>

    suspend fun updateMediaData(deviceMedia: DeviceMedia)

    suspend fun clearMediaTable()

    fun getLocalThumbnailsFromDb(): Flow<List<Thumbnail.LocalThumbnail>>
}
