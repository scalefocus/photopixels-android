package io.photopixels.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class MediaItemsResponse(
    val mediaItems: List<PickedMediaItem>,
    val nextPageToken: String?,
) {
    @Serializable
    data class PickedMediaItem(
        val createTime: String,
        val id: String,
        val mediaFile: MediaFile,
        val type: Type,
    ) {
        @Serializable
        data class MediaFile(
            val baseUrl: String,
            val filename: String,
            val mimeType: String,
        )

        @Serializable
        enum class Type {
            TYPE_UNSPECIFIED,
            PHOTO,
            VIDEO
        }
    }
}
