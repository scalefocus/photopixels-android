package com.scalefocus.data.di

import com.scalefocus.data.repository.AuthRepositoryImpl
import com.scalefocus.data.repository.GooglePhotosRepositoryImpl
import com.scalefocus.data.repository.PhotosRepositoryImpl
import com.scalefocus.data.repository.ServerRepositoryImpl
import com.scalefocus.domain.repository.AuthRepository
import com.scalefocus.domain.repository.GooglePhotosRepository
import com.scalefocus.domain.repository.PhotosRepository
import com.scalefocus.domain.repository.ServerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun providePhotosRepository(impl: PhotosRepositoryImpl): PhotosRepository = impl

    @Provides
    @Singleton
    fun provideServerRepository(impl: ServerRepositoryImpl): ServerRepository = impl

    @Provides
    @Singleton
    fun provideGooglePhotosRepository(impl: GooglePhotosRepositoryImpl): GooglePhotosRepository = impl
}
