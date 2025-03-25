package io.photopixels.domain.model

data class PhotoPickingSession(
    val id: String,
    val expireTime: String,
    val mediaItemsSet: Boolean,
    val pickerUri: String,
    val pollInterval: String,
)
