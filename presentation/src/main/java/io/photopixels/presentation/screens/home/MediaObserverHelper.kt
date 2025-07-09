package io.photopixels.presentation.screens.home

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.domain.model.MediaType
import javax.inject.Inject

/**
 * Helper class to keep track of registered [ContentObserver]s
 */
class MediaObserverHelper @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val contentResolver = context.contentResolver

    val isRegistered: Boolean get() = observers.isNotEmpty()

    private val observers = mutableListOf<ContentObserver>()

    /**
     * Register a new [ContentObserver] for each [MediaType]
     */
    fun registerObserver(observer: (selfChange: Boolean) -> Unit) {
        MediaType.entries.forEach { mediaType ->
            val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    observer(selfChange)
                }
            }
            contentResolver.registerContentObserver(mediaType.mediaUri, true, contentObserver)
            observers.add(contentObserver)
        }
    }

    /**
     * Unregister all registered [ContentObserver]s
     */
    fun unregisterObserver() {
        observers.forEach(contentResolver::unregisterContentObserver)
        observers.clear()
    }
}
