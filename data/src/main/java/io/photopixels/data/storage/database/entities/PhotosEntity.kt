package io.photopixels.data.storage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_photos")
data class PhotosEntity(
    @PrimaryKey
    val id: Long, //  This is from MediaStore.Images.Media._ID
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val contentUri: String,
    val appleCloudId: String,
    val androidCloudId: String, // used for fastHash created locally from the android device
    val hash: String,
    val serverItemHashId: String?, // This hash is available after successful upload
    val isDeleted: Boolean?,
    /** Flag to prevent duplicate uploading on device level */
    val isAlreadyUploaded: Boolean?,
    val dateAdded: Long,
)
