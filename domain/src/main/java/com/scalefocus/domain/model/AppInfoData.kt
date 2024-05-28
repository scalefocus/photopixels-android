package com.scalefocus.domain.model

data class AppInfoData(
    val serverAddress: String,
    val serverVersion: String,
    val appVersion: String,
    val loggedUser: String
)
