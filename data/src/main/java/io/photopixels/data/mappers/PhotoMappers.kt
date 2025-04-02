package io.photopixels.data.mappers

import android.util.Base64
import io.photopixels.data.network.responses.ObjectResponse
import io.photopixels.data.network.responses.ObjectUploadResponse
import io.photopixels.data.storage.database.entities.PhotosEntity
import io.photopixels.data.storage.database.entities.ThumbnailsEntity
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.Thumbnail

fun PhotosEntity.toDomain() = PhotoData(
    id = id.toString(),
    fileName = fileName,
    fileSize = fileSize,
    mimeType = mimeType,
    contentUri = contentUri,
    dateCreated = dateCreated,
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
    dateCreated = dateCreated,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId.orEmpty(),
    hash = hash,
    serverItemHashId = serverItemHashId,
    isDeleted = isDeleted,
    isAlreadyUploaded = isAlreadyUploaded
)

fun ObjectResponse.toDomain() = PhotoUiData(
    id = id,
    hash = originalHash,
    thumbnailByteArray = Base64.decode(thumbnail, Base64.DEFAULT),
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    dateCreated = dateCreated,
)

fun ThumbnailsEntity.toDomain() = PhotoUiData(
    id = id,
    thumbnailByteArray = thumbnailBytes,
    hash = hash,
    isNewlyUploaded = isNewlyUploaded,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    dateCreated = dateCreated,
)

fun PhotoUiData.toEntity() = ThumbnailsEntity(
    id = id,
    thumbnailBytes = thumbnailByteArray,
    hash = hash,
    isNewlyUploaded = isNewlyUploaded,
    androidCloudId = androidCloudId,
    appleCloudId = appleCloudId,
    contentType = "",
    height = 0,
    width = 0,
    dateCreated = dateCreated,
)

fun ObjectUploadResponse.toDomain() = PhotoUploadData(id, revision)

fun PhotosEntity.toThumbnail() = Thumbnail.LocalThumbnail(
    id = id.toString(),
    contentUri = contentUri,
    hash = hash.orEmpty(),
    dateCreated = dateCreated,
)

fun ThumbnailsEntity.toThumbnail() = Thumbnail.RemoteThumbnail(
    id = id,
    thumbnailByteArray = thumbnailBytes,
    hash = hash,
    dateCreated = dateCreated,
    isNewlyUploaded = isNewlyUploaded,
)
