package io.photopixels.domain.model

data class DeviceMedia(
    val id: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val contentUri: String,
    val dateCreated: String,
    val hash: String? = null,
    val appleCloudId: String? = null,
    val androidCloudId: String? = null,
    val serverItemHashId: String? = null,
    val isDeleted: Boolean? = null,
    val isAlreadyUploaded: Boolean? = null
)
