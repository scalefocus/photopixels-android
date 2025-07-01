package io.photopixels.domain.repository

import android.content.Context
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.Thumbnail
import kotlinx.coroutines.flow.Flow

interface DeviceMediaRepository {
    suspend fun insertMediaDataToDb(deviceMediaList: List<DeviceMedia>)

    suspend fun updateMediaDataToDb(deviceMediaList: List<DeviceMedia>)

    fun getDeviceMedia(context: Context): List<DeviceMedia>

    suspend fun getDeviceMediaByHashes(hashes: List<String>): List<DeviceMedia>

    suspend fun getMediaWithMissingHashes(): List<DeviceMedia>

    suspend fun getMediaDataForUploadFromDb(): List<DeviceMedia>

    suspend fun removeMediaDataFromDb(mediaId: Int)

    suspend fun removeMediaDataFromDb(mediaIds: List<Int>)

    suspend fun getMediaDataIdsFromDb(): List<Int>

    suspend fun updateMediaData(deviceMedia: DeviceMedia)

    suspend fun clearMediaTable()

    fun getLocalThumbnailsFromDb(): Flow<List<Thumbnail.LocalThumbnail>>
}
