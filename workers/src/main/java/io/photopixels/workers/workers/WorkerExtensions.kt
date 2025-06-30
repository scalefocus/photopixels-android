package io.photopixels.workers.workers

import android.app.Notification
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker

fun ListenableWorker.setForegroundNotificationAsync(notification: Notification) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        setForegroundAsync(ForegroundInfo(0, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC))
    } else {
        setForegroundAsync(ForegroundInfo(0, notification))
    }
}
