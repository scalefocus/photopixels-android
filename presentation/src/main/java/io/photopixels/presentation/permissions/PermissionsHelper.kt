package io.photopixels.presentation.permissions

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat.checkSelfPermission

object PermissionsHelper {
    fun checkAndRequestPermissions(
        context: Context,
        requestPermissions: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ): Boolean {
        val currentPermission = getStorageAccess(context)

        if (currentPermission == StorageAccess.Denied) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
                requestPermissions.launch(
                    arrayOf(
                        READ_MEDIA_IMAGES,
                        READ_MEDIA_VISUAL_USER_SELECTED,
                        POST_NOTIFICATIONS
                    )
                )
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) { // Android 13
                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, POST_NOTIFICATIONS))
            } else {
                requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE)) // Android < 13
            }
        }

        return currentPermission == StorageAccess.Full || currentPermission == StorageAccess.Partial
    }

    private fun getStorageAccess(context: Context): StorageAccess {
        return if (
            checkSelfPermission(context, READ_MEDIA_IMAGES) == PERMISSION_GRANTED ||
            checkSelfPermission(context, READ_MEDIA_VIDEO) == PERMISSION_GRANTED
        ) {
            // Full access on Android 13+
            StorageAccess.Full
        } else if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(context, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED
        ) {
            // Partial access on Android 13+
            StorageAccess.Partial
        } else if (checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            // Full access up to Android 12
            StorageAccess.Full
        } else {
            // Access denied
            StorageAccess.Denied
        }
    }
}
