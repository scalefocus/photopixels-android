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

    @Query("DELETE from device_photos where id=:photoId")
    suspend fun removePhotoData(photoId: Int)

    @Query("DELETE from device_photos where id in (:photoIds)")
    suspend fun removePhotoData(photoIds: List<Int>)

    @Query("Select * from device_photos")
    fun getPhotosData(): Flow<PhotosEntity>

    @Query(
        "Select * from device_photos where serverItemHashId is null and isDeleted is null " +
            "and isAlreadyUploaded is null and hash is not null order by dateCreated desc"
    )
    fun getPhotosForUpload(): Flow<List<PhotosEntity>>

    @Query(
        "Select * from device_photos where hash is null order by dateCreated desc"
    )
    suspend fun getPhotosWithMissingHashes(): List<PhotosEntity>

    @Query("Select * from device_photos where hash in (:hashes)")
    suspend fun getPhotosByHashes(hashes: List<String>): List<PhotosEntity>

    @Update
    fun updatePhotoData(photosEntity: PhotosEntity)

    @Query("Select id from device_photos")
    suspend fun getPhotosIds(): List<Int>

    @Query("DELETE from device_photos")
    suspend fun clearPhotosTable()
}
