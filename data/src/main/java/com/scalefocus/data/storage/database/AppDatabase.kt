package com.scalefocus.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scalefocus.data.storage.database.entities.PhotosEntity
import com.scalefocus.data.storage.database.entities.ThumbnailsEntity

@Database(entities = [PhotosEntity::class, ThumbnailsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photosDao(): PhotosDao

    abstract fun thumbnailsDao(): ThumbnailsDao
}
