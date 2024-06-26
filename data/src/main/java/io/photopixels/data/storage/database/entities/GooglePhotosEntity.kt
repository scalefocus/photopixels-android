package io.photopixels.data.storage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "google_photos")
data class GooglePhotosEntity(
    @PrimaryKey
    val androidCloudId: String, // Related to Google Photo.ID
    val fileName: String,
    val mimeType: String,
    val baseUrl: String,
    val fileSize: Long? = null,
    val hash: String? = null,
    val appleCloudId: String? = null,
    val serverItemHashId: String? = null, // This hash is available after successful upload
    val isDeleted: Boolean? = null,
    val isAlreadyUploaded: Boolean? = null
)
