package io.photopixels.domain.model

data class AppInfoData(
    val serverAddress: ServerAddress,
    val serverVersion: String,
    val appVersion: String,
    val loggedUser: String
)
