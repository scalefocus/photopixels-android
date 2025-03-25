package io.photopixels.domain.model

sealed class Thumbnail {
    abstract val id: String
    abstract val dateCreated: String
    abstract val hash: String

    data class LocalThumbnail(
        override val id: String,
        override val dateCreated: String,
        override val hash: String,
        val contentUri: String,
    ) : Thumbnail()

    class RemoteThumbnail(
        override val id: String,
        override val dateCreated: String,
        override val hash: String,
        val thumbnailByteArray: ByteArray,
        val isNewlyUploaded: Boolean,
    ) : Thumbnail()
}
