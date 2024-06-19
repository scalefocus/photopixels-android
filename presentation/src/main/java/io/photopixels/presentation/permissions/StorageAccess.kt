package io.photopixels.presentation.permissions

enum class StorageAccess {
    Full,
    Partial, // Only for Android 14+
    Denied
}
