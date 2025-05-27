package io.photopixels.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.photopixels.data.storage.database.entities.ThumbnailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailsDao {

    @Query("SELECT * FROM thumbnails_photos ORDER BY dateCreated DESC")
    fun getAllThumbnails(): Flow<List<ThumbnailsEntity>>

    @Query("SELECT * FROM thumbnails_photos WHERE hash = :hash")
    suspend fun getThumbnailByHash(hash: String): ThumbnailsEntity?

    @Query("SELECT * FROM thumbnails_photos WHERE isNewlyUploaded = 1")
    suspend fun getAllNewlyUploadedThumbnails(): List<ThumbnailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThumbnailPhotos(thumbnailsList: List<ThumbnailsEntity>)

    @Update
    suspend fun updateThumbnailPhotos(thumbnailsList: List<ThumbnailsEntity>)

    @Query("SELECT count(*) FROM thumbnails_photos")
    suspend fun getThumbnailsCount(): Int

    @Query("DELETE FROM thumbnails_photos WHERE id IN (:ids)")
    suspend fun deletePhotos(ids: List<String>)

    @Query("DELETE FROM thumbnails_photos")
    suspend fun clearTable()
}
