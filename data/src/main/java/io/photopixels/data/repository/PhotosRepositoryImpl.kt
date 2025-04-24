package io.photopixels.data.repository

import android.content.Context
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.mappers.toThumbnail
import io.photopixels.data.media.MediaHelper
import io.photopixels.data.network.BackendApi
import io.photopixels.data.storage.database.PhotosDao
import io.photopixels.data.storage.database.ThumbnailsDao
import io.photopixels.data.storage.database.entities.PhotosEntity
import io.photopixels.data.storage.database.entities.ThumbnailsEntity
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoData
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
    private val photosDao: PhotosDao,
    private val thumbnailsDao: ThumbnailsDao,
    private val backendApi: BackendApi,
) : PhotosRepository {

    override suspend fun insertPhotoDataToDB(photoDataList: List<PhotoData>) {
        photosDao.insertPhotoData(photoDataList.map { it.toEntity() })
    }

    override suspend fun updatePhotoDataToDB(photoDataList: List<PhotoData>) {
        photosDao.updatePhotoData(photoDataList.map { it.toEntity() })
    }

    override fun getDevicePhotos(context: Context): List<PhotoData> = MediaHelper.scanPhotos(context)

    override suspend fun getDevicePhotosByHashes(hashes: List<String>): List<PhotoData> =
        photosDao.getPhotosByHashes(hashes).map { it.toDomain() }

    override suspend fun getPhotoByHash(hash: String): PhotoUiData? = thumbnailsDao.getThumbnailByHash(hash)?.toDomain()

    override suspend fun getPhotosWithMissingHashes(): List<PhotoData> =
        photosDao.getPhotosWithMissingHashes().map { it.toDomain() }

    override suspend fun getPhotosDataForUploadFromDB(): List<PhotoData> =
        photosDao.getPhotosForUpload().first().map { photo ->
            photo.toDomain()
        }

    override suspend fun removePhotoDataFromDB(photoId: Int) {
        photosDao.removePhotoData(photoId)
    }

    override suspend fun updatePhotoData(photoData: PhotoData) {
        photosDao.updatePhotoData(photoData.toEntity())
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

    override suspend fun clearPhotosTable() {
        photosDao.clearPhotosTable()
    }

    override suspend fun insertThumbnailsToDb(thumbnailsList: List<PhotoUiData>) {
        thumbnailsDao.insertThumbnailPhotos(thumbnailsList.map { it.toEntity() })
    }

    override fun getLocalThumbnailsFromDb(): Flow<List<Thumbnail.LocalThumbnail>> =
        photosDao.getPhotosForUpload().mapLatest {
            it.map(PhotosEntity::toThumbnail)
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
