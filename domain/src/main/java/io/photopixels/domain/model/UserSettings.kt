package io.photopixels.domain.model

data class UserSettings(
    val syncWithGoogle: Boolean = false,
    val requireWifi: Boolean = true, // by default sync only through Wi-Fi
    val requirePower: Boolean = false
)
