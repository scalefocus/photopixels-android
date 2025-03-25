package io.photopixels.domain.model

data class MediaItems(
    val mediaItems: List<GooglePhoto>,
    val nextPageToken: String?,
)
