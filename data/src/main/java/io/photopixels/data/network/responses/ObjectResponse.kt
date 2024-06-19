package io.photopixels.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ObjectResponse(
    val id: String,
    val thumbnail: String, // Thumbnail encoded in Base64
    val contentType: String,
    val hash: String,
    val appleCloudId: String?,
    val androidCloudId: String?,
    val width: Int,
    val height: Int
)
