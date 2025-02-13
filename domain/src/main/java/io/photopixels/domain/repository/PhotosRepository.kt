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

    suspend fun loadPhotoThumbnail(context: Context, photo: PhotoData): ByteArray

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

    suspend fun savePhotoThumbnailToDb(photo: PhotoData, thumbnailBytes: ByteArray)

    suspend fun getThumbnailsFromDb(): List<PhotoUiData>

    suspend fun getThumbnailsFromDbCount(): Int

    suspend fun clearThumbnailsTable()

    suspend fun deletePhoto(photoServerId: String): Response<Unit>
}
