package io.photopixels.domain.model

data class PhotoData(
    val id: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val contentUri: String,
    val hash: String? = null,
    val appleCloudId: String? = null,
    val androidCloudId: String?,
    val serverItemHashId: String? = null,
    val isDeleted: Boolean? = null,
    val isAlreadyUploaded: Boolean? = null
)
