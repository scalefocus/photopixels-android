package io.photopixels.domain.model

sealed class Thumbnail {
    abstract val id: String
    abstract val dateCreated: String
    abstract val hash: String
    abstract val mediaType: MediaType

    data class LocalThumbnail(
        override val id: String,
        override val dateCreated: String,
        override val hash: String,
        val contentUri: String,
        override val mediaType: MediaType,
    ) : Thumbnail()

    class RemoteThumbnail(
        override val id: String,
        override val dateCreated: String,
        override val hash: String,
        val thumbnailByteArray: ByteArray,
        val isNewlyUploaded: Boolean,
        override val mediaType: MediaType,
    ) : Thumbnail()
}
