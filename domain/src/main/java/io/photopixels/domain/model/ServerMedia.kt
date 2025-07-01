package io.photopixels.domain.model

data class ServerMedia(
    val id: String,
    val thumbnailByteArray: ByteArray,
    val hash: String,
    val isNewlyUploaded: Boolean = false,
    val appleCloudId: String?,
    val androidCloudId: String?,
    val dateCreated: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerMedia

        if (!thumbnailByteArray.contentEquals(other.thumbnailByteArray)) return false
        if (hash != other.hash) return false
        if (isNewlyUploaded != other.isNewlyUploaded) return false
        if (dateCreated != other.dateCreated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbnailByteArray.contentHashCode()
        result = 31 * result + hash.hashCode()
        result = 31 * result + isNewlyUploaded.hashCode()
        result = 31 * result + dateCreated.hashCode()
        return result
    }
}
