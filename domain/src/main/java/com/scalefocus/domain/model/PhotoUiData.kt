package com.scalefocus.domain.model

data class PhotoUiData(
    val id: String,
    val thumbnail: String, // Thumbnail encoded in Base64
    val thumbnailByteArray: ByteArray,
    val hash: String,
    var isNewlyUploaded: Boolean = false,
    val appleCloudId: String?,
    val androidCloudId: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoUiData

        if (thumbnail != other.thumbnail) return false
        if (!thumbnailByteArray.contentEquals(other.thumbnailByteArray)) return false
        if (hash != other.hash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbnail.hashCode()
        result = 31 * result + thumbnailByteArray.contentHashCode()
        result = 31 * result + hash.hashCode()
        return result
    }
}
