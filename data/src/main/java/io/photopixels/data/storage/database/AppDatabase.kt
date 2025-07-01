package io.photopixels.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.photopixels.data.storage.database.entities.DeviceMediaEntity
import io.photopixels.data.storage.database.entities.GooglePhotosEntity
import io.photopixels.data.storage.database.entities.ThumbnailsEntity

@Database(entities = [DeviceMediaEntity::class, ThumbnailsEntity::class, GooglePhotosEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceMediaDao(): DeviceMediaDao

    abstract fun thumbnailsDao(): ThumbnailsDao

    abstract fun googlePhotosDao(): GooglePhotosDao
}
