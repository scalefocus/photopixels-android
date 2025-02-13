package io.photopixels.data.mappers

import android.util.Base64
import io.photopixels.data.network.responses.ObjectResponse
import io.photopixels.data.network.responses.ObjectUploadResponse
import io.photopixels.data.storage.database.entities.PhotosEntity
import io.photopixels.data.storage.database.entities.ThumbnailsEntity
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData

fun PhotosEntity.toDomain() = PhotoData(
    id = id,
    fileName = fileName,
    fileSize = fileSize,
    mimeType = mimeType,
    contentUri = contentUri,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    hash = hash,
    serverItemHashId = serverItemHashId,
    isDeleted = isDeleted,
    dateAdded = dateAdded,
)

fun PhotoData.toEntity() = PhotosEntity(
    id = id,
    fileName = fileName,
    contentUri = contentUri,
    fileSize = fileSize,
    mimeType = mimeType,
    androidCloudId = androidCloudId ?: "",
    appleCloudId = appleCloudId ?: "",
    hash = hash ?: "",
    serverItemHashId = serverItemHashId,
    isDeleted = isDeleted,
    isAlreadyUploaded = isAlreadyUploaded,
    dateAdded = dateAdded,
)

fun PhotoData.toEntity(thumbnailBytes: ByteArray) = ThumbnailsEntity(
    id = id.toString(),
    thumbnailBytes = thumbnailBytes,
    hash = "",
    localUri = contentUri,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    contentType = "",
    height = 0,
    width = 0
)

fun ObjectResponse.toDomain() = PhotoUiData(
    id = id,
    thumbnail = thumbnail,
    hash = hash,
    thumbnailByteArray = Base64.decode(thumbnail, Base64.DEFAULT),
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId
)

fun ThumbnailsEntity.toDomain() = PhotoUiData(
    id = id,
    thumbnailByteArray = thumbnailBytes,
    thumbnail = "",
    hash = hash,
    localUri = localUri,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId
)

fun PhotoUiData.toEntity() = ThumbnailsEntity(
    id = id,
    thumbnailBytes = thumbnailByteArray,
    hash = hash,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    localUri = localUri,
    contentType = "",
    height = 0,
    width = 0
)

fun ObjectUploadResponse.toDomain() = PhotoUploadData(id, revision)
