package com.scalefocus.data.storage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thumbnails_photos")
data class ThumbnailsEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val thumbnailBytes: ByteArray,
    val contentType: String,
    val hash: String,
    val appleCloudId: String?,
    val androidCloudId: String?,
    val width: Int,
    val height: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ThumbnailsEntity

        if (id != other.id) return false
        if (!thumbnailBytes.contentEquals(other.thumbnailBytes)) return false
        if (contentType != other.contentType) return false
        if (hash != other.hash) return false
        if (appleCloudId != other.appleCloudId) return false
        if (androidCloudId != other.androidCloudId) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + thumbnailBytes.contentHashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + hash.hashCode()
        result = 31 * result + (appleCloudId?.hashCode() ?: 0)
        result = 31 * result + (androidCloudId?.hashCode() ?: 0)
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}
