package io.photopixels.domain.model

data class UserSettings(
    val syncWithGoogle: Boolean = false,
    val requireWifi: Boolean = false,
    val requirePower: Boolean = false
)
