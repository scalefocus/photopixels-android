package io.photopixels.presentation.screens.home

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaObserverHelper @Inject constructor(
    @ApplicationContext context: Context,
    private val uri: Uri,
) {

    private val contentResolver = context.contentResolver

    fun registerObserver(observer: (selfChange: Boolean) -> Unit): ContentObserver {
        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                observer(selfChange)
            }
        }
        contentResolver.registerContentObserver(uri, true, contentObserver)
        return contentObserver
    }

    fun unregisterObserver(contentObserver: ContentObserver) {
        contentResolver.unregisterContentObserver(contentObserver)
    }
}
