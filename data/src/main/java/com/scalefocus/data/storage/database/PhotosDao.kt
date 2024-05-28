package com.scalefocus.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.scalefocus.data.storage.database.entities.PhotosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotoData(photosEntity: List<PhotosEntity>)

    @Query("DELETE from device_photos where id=:photoId")
    suspend fun removePhotoData(photoId: Int)

    @Query("Select * from device_photos")
    fun getPhotosData(): Flow<PhotosEntity>

    @Query(
        "Select * from device_photos where serverItemHashId is null and isDeleted is null and isAlreadyUploaded is null"
    )
    fun getPhotosForUpload(): List<PhotosEntity>

    @Update
    fun updatePhotoData(photosEntity: PhotosEntity)

    @Query("DELETE from device_photos")
    suspend fun clearPhotosTable()
}
