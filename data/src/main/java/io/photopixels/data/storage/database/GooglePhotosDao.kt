package io.photopixels.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.photopixels.data.storage.database.entities.GooglePhotosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GooglePhotosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotosData(photosEntity: List<GooglePhotosEntity>)

    @Query("DELETE from google_photos where androidCloudId=:androidCloudId")
    suspend fun removePhotoData(androidCloudId: Int)

    @Query("Select * from google_photos")
    fun getPhotosData(): Flow<GooglePhotosEntity>

    @Query(
        "Select * from google_photos where serverItemHashId is null and isDeleted is null and isAlreadyUploaded is null"
    )
    fun getPhotosForUpload(): List<GooglePhotosEntity>

    @Update
    fun updatePhotoData(googlePhotoData: GooglePhotosEntity)

    @Query("DELETE from google_photos")
    suspend fun clearTable()
}
