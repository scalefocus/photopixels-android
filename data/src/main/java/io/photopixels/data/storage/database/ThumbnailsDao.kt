package io.photopixels.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.photopixels.data.storage.database.entities.ThumbnailsEntity

@Dao
interface ThumbnailsDao {

    @Query("SELECT * FROM thumbnails_photos")
    suspend fun getAllThumbnails(): List<ThumbnailsEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertThumbnailPhotos(thumbnailsList: List<ThumbnailsEntity>)

    @Query("Select count(*) from thumbnails_photos")
    suspend fun getThumbnailsCount(): Int

    @Query("DELETE from thumbnails_photos")
    suspend fun clearTable()
}
