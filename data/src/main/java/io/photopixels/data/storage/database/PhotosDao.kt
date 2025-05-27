package io.photopixels.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.photopixels.data.storage.database.entities.PhotosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotoData(photosEntity: List<PhotosEntity>)

    @Update
    suspend fun updatePhotoData(photosEntity: List<PhotosEntity>)

    @Query("DELETE FROM device_photos WHERE id=:photoId")
    suspend fun removePhotoData(photoId: Int)

    @Query("DELETE FROM device_photos WHERE id in (:photoIds)")
    suspend fun removePhotoData(photoIds: List<Int>)

    @Query("SELECT * FROM device_photos")
    fun getPhotosData(): Flow<PhotosEntity>

    @Query(
        "SELECT * FROM device_photos WHERE serverItemHashId IS NULL AND isDeleted IS NULL AND " +
            "isAlreadyUploaded IS NULL AND hash IS NOT NULL ORDER BY dateCreated DESC"
    )
    fun getPhotosForUpload(): Flow<List<PhotosEntity>>

    @Query(
        "SELECT * FROM device_photos WHERE hash IS NULL ORDER BY dateCreated DESC"
    )
    suspend fun getPhotosWithMissingHashes(): List<PhotosEntity>

    @Query("SELECT * FROM device_photos WHERE hash IN (:hashes)")
    suspend fun getPhotosByHashes(hashes: List<String>): List<PhotosEntity>

    @Update
    fun updatePhotoData(photosEntity: PhotosEntity)

    @Query("SELECT id FROM device_photos")
    suspend fun getPhotosIds(): List<Int>

    @Query("DELETE FROM device_photos")
    suspend fun clearPhotosTable()
}
