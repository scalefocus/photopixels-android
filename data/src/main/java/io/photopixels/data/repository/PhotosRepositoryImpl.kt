package io.photopixels.data.repository

import android.content.Context
import android.net.Uri
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.mappers.toThumbnail
import io.photopixels.data.media.MediaHelper
import io.photopixels.data.network.BackendApi
import io.photopixels.data.network.tus.ResumableUploadApi
import io.photopixels.data.storage.database.DeviceMediaDao
import io.photopixels.data.storage.database.ThumbnailsDao
import io.photopixels.data.storage.database.entities.DeviceMediaEntity
import io.photopixels.data.storage.database.entities.ThumbnailsEntity
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@Suppress("TooManyFunctions")
class PhotosRepositoryImpl @Inject constructor(
    private val deviceMediaDao: DeviceMediaDao,
    private val thumbnailsDao: ThumbnailsDao,
    private val backendApi: BackendApi,
    private val resumableUploadApi: ResumableUploadApi,
    private val mediaHelper: MediaHelper,
) : PhotosRepository {

    override suspend fun insertPhotoDataToDB(deviceMediaList: List<DeviceMedia>) {
        deviceMediaDao.insertMediaData(deviceMediaList.map { it.toEntity() })
    }

    override suspend fun updatePhotoDataToDB(deviceMediaList: List<DeviceMedia>) {
        deviceMediaDao.updateMediaData(deviceMediaList.map { it.toEntity() })
    }

    override fun getDeviceMedia(context: Context): List<DeviceMedia> = mediaHelper.scanPhotos(context)

    override suspend fun getDevicePhotosByHashes(hashes: List<String>): List<DeviceMedia> =
        deviceMediaDao.getMediaByHashes(hashes).map { it.toDomain() }

    override suspend fun getPhotoByHash(hash: String): PhotoUiData? = thumbnailsDao.getThumbnailByHash(hash)?.toDomain()

    override suspend fun getPhotosWithMissingHashes(): List<DeviceMedia> =
        deviceMediaDao.getMediaWithMissingHashes().map { it.toDomain() }

    override suspend fun getPhotosDataForUploadFromDB(): List<DeviceMedia> =
        deviceMediaDao.getMediaForUpload().first().map { photo ->
            photo.toDomain()
        }

    override suspend fun removePhotoDataFromDB(mediaId: Int) {
        deviceMediaDao.removeMediaData(mediaId)
    }

    override suspend fun removePhotosDataFromDB(mediaIds: List<Int>) {
        deviceMediaDao.removeMediaData(mediaIds)
    }

    override suspend fun getPhotosDataIdsFromDB(): List<Int> = deviceMediaDao.getMediaIds()

    override suspend fun updatePhotoData(deviceMedia: DeviceMedia) {
        deviceMediaDao.updateMediaData(deviceMedia.toEntity())
    }

    override suspend fun getServerThumbnails(serverItemHashIds: List<String>): Response<List<PhotoUiData>> = backendApi
        .getThumbnailsByIds(
            serverItemHashIds
        )

    override suspend fun uploadPhoto(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData> = backendApi.uploadPhoto(fileBytes, fileName, mimeType, androidCloudId, objectHash)

    override suspend fun uploadPhoto(
        uri: Uri,
        fileName: String,
        objectHash: String,
    ) = resumableUploadApi.uploadFile(uri, fileName, objectHash)

    override suspend fun clearPhotosTable() {
        deviceMediaDao.clearMediaTable()
    }

    override suspend fun insertThumbnailsToDb(thumbnailsList: List<PhotoUiData>) {
        thumbnailsDao.insertThumbnailPhotos(thumbnailsList.map { it.toEntity() })
    }

    override fun getLocalThumbnailsFromDb(): Flow<List<Thumbnail.LocalThumbnail>> =
        deviceMediaDao.getMediaForUpload().mapLatest {
            it.map(DeviceMediaEntity::toThumbnail)
        }

    override fun getRemoteThumbnailsFromDb(): Flow<List<Thumbnail.RemoteThumbnail>> =
        thumbnailsDao.getAllThumbnails().mapLatest {
            it.map(ThumbnailsEntity::toThumbnail)
        }

    override suspend fun clearNewlyUploadedThumbnails() {
        thumbnailsDao.getAllNewlyUploadedThumbnails()
            .map { it.copy(isNewlyUploaded = false) }
            .takeIf { it.isNotEmpty() }
            ?.let { thumbnailsDao.updateThumbnailPhotos(it) }
    }

    override suspend fun getThumbnailsFromDbCount(): Int = thumbnailsDao.getThumbnailsCount()

    override suspend fun deleteThumbnailsFromDb(ids: List<String>) {
        thumbnailsDao.deletePhotos(ids)
    }

    override suspend fun clearThumbnailsTable() {
        thumbnailsDao.clearTable()
    }

    override suspend fun deletePhoto(photoServerId: String): Response<Unit> = backendApi.deletePhoto(photoServerId)
}
