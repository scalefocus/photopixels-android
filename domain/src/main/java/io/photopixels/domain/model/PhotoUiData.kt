package io.photopixels.domain.model

data class PhotoUiData(
    val id: String,
    val thumbnailByteArray: ByteArray,
    val hash: String,
    val isNewlyUploaded: Boolean = false,
    val appleCloudId: String?,
    val androidCloudId: String?,
    val dateTaken: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoUiData

        if (!thumbnailByteArray.contentEquals(other.thumbnailByteArray)) return false
        if (hash != other.hash) return false
        if (isNewlyUploaded != other.isNewlyUploaded) return false
        if (dateTaken != other.dateTaken) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbnailByteArray.contentHashCode()
        result = 31 * result + hash.hashCode()
        result = 31 * result + isNewlyUploaded.hashCode()
        result = 31 * result + dateTaken.hashCode()
        return result
    }
}
