package com.scalefocus.di

import com.scalefocus.domain.workers.WorkerStarter
import com.scalefocus.workers.WorkerStarterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WorkerModule {

    @Provides
    @Singleton
    fun provideWorkerStarter(impl: WorkerStarterImpl): WorkerStarter = impl
}
