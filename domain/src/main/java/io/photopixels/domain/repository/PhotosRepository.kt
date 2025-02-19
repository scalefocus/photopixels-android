package io.photopixels.domain.repository

import android.content.Context
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {
    suspend fun insertPhotoDataToDB(photoDataList: List<PhotoData>)

    fun getPhotosDataFromDB(): Flow<Unit>

    fun getDevicePhotos(context: Context): List<PhotoData>

    fun getPhotosDataForUploadFromDB(): List<PhotoData>

    suspend fun removePhotoDataFromDB(photoId: Int)

    suspend fun updatePhotoData(photoData: PhotoData)

    suspend fun getServerThumbnails(serverItemHashIds: List<String>): Response<List<PhotoUiData>>

    suspend fun uploadPhoto(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData>

    suspend fun setAllPreviewPhotosIdsInMemory(photosIds: List<String>)

    suspend fun getAllPreviewPhotosIdsFromMemory(): List<String>

    suspend fun clearPhotosTable()

    suspend fun insertThumbnailsToDb(thumbnailsList: List<PhotoUiData>)

    fun getThumbnailsFromDb(): Flow<List<PhotoUiData>>

    suspend fun clearNewlyUploadedThumbnails()

    suspend fun getThumbnailsFromDbCount(): Int

    suspend fun deleteThumbnailsFromDb(ids: List<String>)

    suspend fun clearThumbnailsTable()

    suspend fun deletePhoto(photoServerId: String): Response<Unit>
}
