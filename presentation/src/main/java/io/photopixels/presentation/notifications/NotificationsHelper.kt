package io.photopixels.presentation.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel(channelId: String, channelTitle: String, channelDescription: String) {
        val channel = NotificationChannel(
            channelId,
            channelTitle,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = channelDescription
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(
        channelId: String,
        notificationTitle: String,
        notificationContent: String,
        notificationIconId: Int = android.R.drawable.ic_popup_sync,
        isOngoing: Boolean = true,
        autoCancel: Boolean = false
    ): Notification = NotificationCompat
        .Builder(
            context,
            channelId
        ).setContentTitle(notificationTitle)
        .setContentText(notificationContent)
        .setAutoCancel(autoCancel)
        .setSmallIcon(notificationIconId)
        .setOngoing(isOngoing)
        .build()

    @SuppressLint("MissingPermission")
    fun showNotification(notificationId: Int, notification: Notification) {
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
