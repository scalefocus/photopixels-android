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

    @Query("DELETE FROM google_photos WHERE androidCloudId=:androidCloudId")
    suspend fun removePhotoData(androidCloudId: Int)

    @Query("SELECT * FROM google_photos")
    fun getPhotosData(): Flow<GooglePhotosEntity>

    @Query(
        "SELECT * FROM google_photos " +
            "WHERE serverItemHashId IS null AND isDeleted IS null AND isAlreadyUploaded IS null"
    )
    fun getPhotosForUpload(): List<GooglePhotosEntity>

    @Update
    fun updatePhotoData(googlePhotoData: GooglePhotosEntity)

    @Query("DELETE FROM google_photos")
    suspend fun clearTable()
}
