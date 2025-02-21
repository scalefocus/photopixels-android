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

    @Query("SELECT * FROM thumbnails_photos order by dateTaken desc")
    fun getAllThumbnails(): Flow<List<ThumbnailsEntity>>

    @Query("SELECT * FROM thumbnails_photos where isNewlyUploaded = 1")
    suspend fun getAllNewlyUploadedThumbnails(): List<ThumbnailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThumbnailPhotos(thumbnailsList: List<ThumbnailsEntity>)

    @Update
    suspend fun updateThumbnailPhotos(thumbnailsList: List<ThumbnailsEntity>)

    @Query("Select count(*) from thumbnails_photos")
    suspend fun getThumbnailsCount(): Int

    @Query("DELETE from thumbnails_photos where id in (:ids)")
    suspend fun deletePhotos(ids: List<String>)

    @Query("DELETE from thumbnails_photos")
    suspend fun clearTable()
}
