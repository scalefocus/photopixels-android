package io.photopixels.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.photopixels.data.repository.AuthRepositoryImpl
import io.photopixels.data.repository.DeviceMediaRepositoryImpl
import io.photopixels.data.repository.GooglePhotosRepositoryImpl
import io.photopixels.data.repository.ServerMediaRepositoryImpl
import io.photopixels.data.repository.ServerRepositoryImpl
import io.photopixels.data.repository.UserSettingsRepositoryImpl
import io.photopixels.domain.repository.AuthRepository
import io.photopixels.domain.repository.DeviceMediaRepository
import io.photopixels.domain.repository.GooglePhotosRepository
import io.photopixels.domain.repository.ServerMediaRepository
import io.photopixels.domain.repository.ServerRepository
import io.photopixels.domain.repository.UserSettingsRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    internal fun provideDeviceMediaRepository(impl: DeviceMediaRepositoryImpl): DeviceMediaRepository = impl

    @Provides
    @Singleton
    internal fun provideServerMediaRepository(impl: ServerMediaRepositoryImpl): ServerMediaRepository = impl

    @Provides
    @Singleton
    fun provideServerRepository(impl: ServerRepositoryImpl): ServerRepository = impl

    @Provides
    @Singleton
    fun provideGooglePhotosRepository(impl: GooglePhotosRepositoryImpl): GooglePhotosRepository = impl

    @Provides
    @Singleton
    fun provideUserSettingsRepository(impl: UserSettingsRepositoryImpl): UserSettingsRepository = impl
}
