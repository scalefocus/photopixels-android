package io.photopixels.data.repository

import android.content.Context
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.media.MediaHelper
import io.photopixels.data.network.BackendApi
import io.photopixels.data.storage.database.PhotosDao
import io.photopixels.data.storage.database.ThumbnailsDao
import io.photopixels.data.storage.memory.MemoryStorage
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Suppress("TooManyFunctions")
class PhotosRepositoryImpl @Inject constructor(
    private val photosDao: PhotosDao,
    private val thumbnailsDao: ThumbnailsDao,
    private val backendApi: BackendApi,
    private val memoryStorage: MemoryStorage
) : PhotosRepository {

    override suspend fun insertPhotoDataToDB(photoDataList: List<PhotoData>) {
        photosDao.insertPhotoData(photoDataList.map { it.toEntity() })
    }

    override fun getPhotosDataFromDB(): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override fun getDevicePhotos(context: Context): List<PhotoData> = MediaHelper.scanPhotosAndGenerateHashes(context)

    override fun getPhotosDataForUploadFromDB(): List<PhotoData> = photosDao.getPhotosForUpload().map { it.toDomain() }

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

    override suspend fun setAllPreviewPhotosIdsInMemory(photosIds: List<String>) {
        memoryStorage.addPhotosServerIdsList(photosIds)
    }

    override suspend fun getAllPreviewPhotosIdsFromMemory(): List<String> = memoryStorage.photosIdsList

    override suspend fun clearPhotosTable() {
        photosDao.clearPhotosTable()
    }

    override suspend fun insertThumbnailsToDb(thumbnailsList: List<PhotoUiData>) {
        thumbnailsDao.insertThumbnailPhotos(thumbnailsList.map { it.toEntity() })
    }

    override suspend fun getThumbnailsFromDb(): List<PhotoUiData> = thumbnailsDao.getAllThumbnails().map {
        it.toDomain()
    }

    override suspend fun getThumbnailsFromDbCount(): Int = thumbnailsDao.getThumbnailsCount()

    override suspend fun clearThumbnailsTable() {
        thumbnailsDao.clearTable()
    }

    override suspend fun deletePhoto(photoServerId: String): Response<Unit> = backendApi.deletePhoto(photoServerId)
}
