package com.scalefocus.data.di

import android.content.Context
import androidx.room.Room
import com.scalefocus.data.storage.database.AppDatabase
import com.scalefocus.data.storage.database.PhotosDao
import com.scalefocus.data.storage.database.ThumbnailsDao
import com.scalefocus.data.storage.datastore.AuthDataStore
import com.scalefocus.data.storage.datastore.CipherUtil
import com.scalefocus.data.storage.datastore.UserPreferencesDataStore
import com.scalefocus.data.storage.memory.MemoryStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class StorageModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext applicationContext: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "photopixels-database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePhotosDao(appDatabase: AppDatabase): PhotosDao = appDatabase.photosDao()

    @Provides
    @Singleton
    fun provideThumbnailsDao(appDatabase: AppDatabase): ThumbnailsDao = appDatabase.thumbnailsDao()

    @Provides
    @Singleton
    fun provideCipherUtil() = CipherUtil()

    @Provides
    @Singleton
    fun provideAuthDataStore(
        @ApplicationContext applicationContext: Context,
        cipherUtil: CipherUtil
    ) = AuthDataStore(
        applicationContext,
        cipherUtil
    )

    @Provides
    @Singleton
    fun provideUserPrefsDataStore(
        @ApplicationContext applicationContext: Context,
        cipherUtil: CipherUtil
    ) = UserPreferencesDataStore(
        applicationContext,
        cipherUtil
    )

    @Provides
    @Singleton
    fun providesMemoryStorage() = MemoryStorage()
}
