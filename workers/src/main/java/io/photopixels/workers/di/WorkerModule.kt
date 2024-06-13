package io.photopixels.workers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.photopixels.domain.workers.WorkerStarter
import io.photopixels.workers.WorkerStarterImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WorkerModule {

    @Provides
    @Singleton
    fun provideWorkerStarter(impl: WorkerStarterImpl): WorkerStarter = impl
}
