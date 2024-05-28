package com.scalefocus.presentation.permissions

enum class StorageAccess {
    Full,
    Partial, // Only for Android 14+
    Denied
}
