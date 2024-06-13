package io.photopixels.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.photopixels.data.storage.database.entities.PhotosEntity
import io.photopixels.data.storage.database.entities.ThumbnailsEntity

@Database(entities = [PhotosEntity::class, ThumbnailsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photosDao(): PhotosDao

    abstract fun thumbnailsDao(): ThumbnailsDao
}
