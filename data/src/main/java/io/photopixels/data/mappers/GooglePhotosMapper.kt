package io.photopixels.data.mappers

import com.google.photos.types.proto.MediaItem
import io.photopixels.data.storage.database.entities.GooglePhotosEntity
import io.photopixels.domain.model.GooglePhoto

fun GooglePhotosEntity.toDomain() = GooglePhoto(
    androidCloudId = androidCloudId,
    fileName = fileName,
    mimeType = mimeType,
    baseUrl = baseUrl,
    hash = hash,
    serverItemHashId = serverItemHashId,
    isAlreadyUploaded = isAlreadyUploaded
)

fun GooglePhoto.toEntity() = GooglePhotosEntity(
    androidCloudId = androidCloudId,
    fileName = fileName,
    mimeType = mimeType,
    baseUrl = baseUrl,
    hash = hash,
    serverItemHashId = serverItemHashId,
    isAlreadyUploaded = isAlreadyUploaded,
    fileSize = null,
    isDeleted = null
)

fun MediaItem.toEntity() = GooglePhotosEntity(
    androidCloudId = id,
    fileName = filename,
    mimeType = mimeType,
    baseUrl = baseUrl,
    hash = null,
    serverItemHashId = null,
    isAlreadyUploaded = null
)
