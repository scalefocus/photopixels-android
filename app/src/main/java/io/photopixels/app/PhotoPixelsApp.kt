package io.photopixels.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor2.KtorNetworkFetcherFactory
import dagger.hilt.android.HiltAndroidApp
import io.ktor.client.HttpClient
import io.photopixels.data.di.NetworkModule.Companion.BACKEND_API_HTTP_CLIENT
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class PhotoPixelsApp : Application(), Configuration.Provider, SingletonImageLoader.Factory {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    @Named(BACKEND_API_HTTP_CLIENT)
    lateinit var httpClient: HttpClient

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(this)
        .components { add(KtorNetworkFetcherFactory(httpClient)) }
        .build()
}
