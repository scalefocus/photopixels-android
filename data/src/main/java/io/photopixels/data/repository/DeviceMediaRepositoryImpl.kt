package io.photopixels.data.repository

import android.content.Context
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.mappers.toThumbnail
import io.photopixels.data.media.MediaHelper
import io.photopixels.data.storage.database.DeviceMediaDao
import io.photopixels.data.storage.database.entities.DeviceMediaEntity
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.repository.DeviceMediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

internal class DeviceMediaRepositoryImpl @Inject constructor(
    private val deviceMediaDao: DeviceMediaDao,
    private val mediaHelper: MediaHelper,
) : DeviceMediaRepository {

    override suspend fun insertMediaDataToDB(deviceMediaList: List<DeviceMedia>) {
        deviceMediaDao.insertMediaData(deviceMediaList.map { it.toEntity() })
    }

    override suspend fun updateMediaDataToDB(deviceMediaList: List<DeviceMedia>) {
        deviceMediaDao.updateMediaData(deviceMediaList.map { it.toEntity() })
    }

    override fun getDeviceMedia(context: Context): List<DeviceMedia> = mediaHelper.scanPhotos(context)

    override suspend fun getDeviceMediaByHashes(hashes: List<String>): List<DeviceMedia> =
        deviceMediaDao.getMediaByHashes(hashes).map { it.toDomain() }

    override suspend fun getMediaWithMissingHashes(): List<DeviceMedia> =
        deviceMediaDao.getMediaWithMissingHashes().map { it.toDomain() }

    override suspend fun getMediaDataForUploadFromDB(): List<DeviceMedia> =
        deviceMediaDao.getMediaForUpload().first().map { photo ->
            photo.toDomain()
        }

    override suspend fun removeMediaDataFromDB(mediaId: Int) {
        deviceMediaDao.removeMediaData(mediaId)
    }

    override suspend fun removeMediaDataFromDB(mediaIds: List<Int>) {
        deviceMediaDao.removeMediaData(mediaIds)
    }

    override suspend fun getMediaDataIdsFromDB(): List<Int> = deviceMediaDao.getMediaIds()

    override suspend fun updateMediaData(deviceMedia: DeviceMedia) {
        deviceMediaDao.updateMediaData(deviceMedia.toEntity())
    }

    override suspend fun clearMediaTable() {
        deviceMediaDao.clearMediaTable()
    }

    override fun getLocalThumbnailsFromDb(): Flow<List<Thumbnail.LocalThumbnail>> =
        deviceMediaDao.getMediaForUpload().mapLatest {
            it.map(DeviceMediaEntity::toThumbnail)
        }
}
