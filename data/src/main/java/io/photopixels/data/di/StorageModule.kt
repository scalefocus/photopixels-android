package io.photopixels.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.photopixels.data.storage.database.AppDatabase
import io.photopixels.data.storage.database.GooglePhotosDao
import io.photopixels.data.storage.database.PhotosDao
import io.photopixels.data.storage.database.ThumbnailsDao
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.data.storage.datastore.CipherUtil
import io.photopixels.data.storage.datastore.UserPreferencesDataStore
import io.photopixels.data.storage.memory.MemoryStorage
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class StorageModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext applicationContext: Context
    ): AppDatabase = Room
        .databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "photopixels-database"
        ).build()

    @Provides
    @Singleton
    fun providePhotosDao(appDatabase: AppDatabase): PhotosDao = appDatabase.photosDao()

    @Provides
    @Singleton
    fun provideThumbnailsDao(appDatabase: AppDatabase): ThumbnailsDao = appDatabase.thumbnailsDao()

    @Provides
    @Singleton
    fun provideGooglePhotosDao(appDatabase: AppDatabase): GooglePhotosDao = appDatabase.googlePhotosDao()

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
