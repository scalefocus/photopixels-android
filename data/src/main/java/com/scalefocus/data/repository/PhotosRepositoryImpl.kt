package com.scalefocus.data.repository

import android.content.Context
import com.scalefocus.data.mappers.toDomain
import com.scalefocus.data.mappers.toEntity
import com.scalefocus.data.media.MediaHelper
import com.scalefocus.data.network.BackendApi
import com.scalefocus.data.storage.database.PhotosDao
import com.scalefocus.data.storage.database.ThumbnailsDao
import com.scalefocus.data.storage.memory.MemoryStorage
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.PhotoData
import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.model.PhotoUploadData
import com.scalefocus.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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

    override fun getDevicePhotos(context: Context): List<PhotoData> {
        return MediaHelper.scanPhotosAndGenerateHashes(context)
    }

    override fun getPhotosDataForUploadFromDB(): List<PhotoData> {
        return photosDao.getPhotosForUpload().map { it.toDomain() }
    }

    override suspend fun removePhotoDataFromDB(photoId: Int) {
        photosDao.removePhotoData(photoId)
    }

    override suspend fun updatePhotoData(photoData: PhotoData) {
        photosDao.updatePhotoData(photoData.toEntity())
    }

    override suspend fun getServerThumbnails(serverItemHashIds: List<String>): Response<List<PhotoUiData>> {
        return backendApi.getThumbnailsByIds(serverItemHashIds)
    }

    override suspend fun uploadPhoto(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData> {
        return backendApi.uploadPhoto(fileBytes, fileName, mimeType, androidCloudId, objectHash)
    }

    override suspend fun setAllPreviewPhotosIdsInMemory(photosIds: List<String>) {
        memoryStorage.addPhotosServerIdsList(photosIds)
    }

    override suspend fun getAllPreviewPhotosIdsFromMemory(): List<String> {
        return memoryStorage.photosIdsList
    }

    override suspend fun clearPhotosTable() {
        photosDao.clearPhotosTable()
    }

    override suspend fun insertThumbnailsToDb(thumbnailsList: List<PhotoUiData>) {
        thumbnailsDao.insertThumbnailPhotos(thumbnailsList.map { it.toEntity() })
    }

    override suspend fun getThumbnailsFromDb(): List<PhotoUiData> {
        return thumbnailsDao.getAllThumbnails().map { it.toDomain() }
    }

    override suspend fun clearThumbnailsTable() {
        thumbnailsDao.clearTable()
    }
}
