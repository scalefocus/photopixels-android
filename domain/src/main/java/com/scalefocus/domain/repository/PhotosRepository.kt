package com.scalefocus.domain.repository

import android.content.Context
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.PhotoData
import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.model.PhotoUploadData
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

    suspend fun getThumbnailsFromDb(): List<PhotoUiData>

    suspend fun clearThumbnailsTable()
}
