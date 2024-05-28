package com.scalefocus.data.mappers

import android.util.Base64
import com.scalefocus.data.network.responses.ObjectResponse
import com.scalefocus.data.network.responses.ObjectUploadResponse
import com.scalefocus.data.storage.database.entities.PhotosEntity
import com.scalefocus.data.storage.database.entities.ThumbnailsEntity
import com.scalefocus.domain.model.PhotoData
import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.model.PhotoUploadData

fun PhotosEntity.toDomain() = PhotoData(
    id = id.toString(),
    fileName = fileName,
    fileSize = fileSize,
    mimeType = mimeType,
    contentUri = contentUri,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    hash = hash,
    serverItemHashId = serverItemHashId,
    isDeleted = isDeleted
)

fun PhotoData.toEntity() = PhotosEntity(
    id = Integer.valueOf(id),
    fileName = fileName,
    contentUri = contentUri,
    fileSize = fileSize,
    mimeType = mimeType,
    androidCloudId = androidCloudId ?: "",
    appleCloudId = appleCloudId ?: "",
    hash = hash ?: "",
    serverItemHashId = serverItemHashId,
    isDeleted = isDeleted,
    isAlreadyUploaded = isAlreadyUploaded
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
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId
)

fun PhotoUiData.toEntity() = ThumbnailsEntity(
    id = id,
    thumbnailBytes = thumbnailByteArray,
    hash = hash,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    contentType = "",
    height = 0,
    width = 0
)

fun ObjectUploadResponse.toDomain() = PhotoUploadData(id, revision)
