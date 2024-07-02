package io.photopixels.domain.model

data class GooglePhoto(
    val androidCloudId: String,
    val fileName: String,
    val mimeType: String,
    val baseUrl: String,
    val hash: String?,
    val serverItemHashId: String?,
    val isAlreadyUploaded: Boolean?,
)
