package io.photopixels.data.mappers

import android.util.Base64
import io.photopixels.data.network.responses.ObjectResponse
import io.photopixels.data.network.responses.ObjectUploadResponse
import io.photopixels.data.storage.database.entities.DeviceMediaEntity
import io.photopixels.data.storage.database.entities.ThumbnailsEntity
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.MediaType
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.model.Thumbnail

fun DeviceMediaEntity.toDomain() = DeviceMedia(
    id = id,
    fileName = fileName,
    fileSize = fileSize,
    mediaType = mediaType,
    mimeType = mimeType,
    contentUri = contentUri,
    dateCreated = dateCreated,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    hash = hash,
    serverItemHashId = serverItemHashId,
    isDeleted = isDeleted
)

fun DeviceMedia.toEntity() = DeviceMediaEntity(
    id = id,
    fileName = fileName,
    contentUri = contentUri,
    fileSize = fileSize,
    mediaType = mediaType,
    mimeType = mimeType,
    dateCreated = dateCreated,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId.orEmpty(),
    hash = hash,
    serverItemHashId = serverItemHashId,
    isDeleted = isDeleted,
    isAlreadyUploaded = isAlreadyUploaded
)

fun ObjectResponse.toDomain() = ServerMedia(
    id = id,
    hash = originalHash,
    mediaType = if (contentType.startsWith("video")) MediaType.VIDEO else MediaType.IMAGE,
    thumbnailByteArray = Base64.decode(thumbnail, Base64.DEFAULT),
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    dateCreated = dateCreated,
)

fun ThumbnailsEntity.toDomain() = ServerMedia(
    id = id,
    thumbnailByteArray = thumbnailBytes,
    hash = hash,
    mediaType = mediaType,
    isNewlyUploaded = isNewlyUploaded,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    dateCreated = dateCreated,
)

fun ServerMedia.toEntity() = ThumbnailsEntity(
    id = id,
    thumbnailBytes = thumbnailByteArray,
    hash = hash,
    isNewlyUploaded = isNewlyUploaded,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    mediaType = mediaType,
    dateCreated = dateCreated,
)

fun ObjectUploadResponse.toDomain() = PhotoUploadData(id, revision)

fun DeviceMediaEntity.toThumbnail() = Thumbnail.LocalThumbnail(
    id = id.toString(),
    contentUri = contentUri,
    hash = hash.orEmpty(),
    dateCreated = dateCreated,
    mediaType = mediaType,
)

fun ThumbnailsEntity.toThumbnail() = Thumbnail.RemoteThumbnail(
    id = id,
    thumbnailByteArray = thumbnailBytes,
    hash = hash,
    dateCreated = dateCreated,
    isNewlyUploaded = isNewlyUploaded,
    mediaType = mediaType,
)
