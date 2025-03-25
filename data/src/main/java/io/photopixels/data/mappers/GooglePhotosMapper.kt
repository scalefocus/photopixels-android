package io.photopixels.data.mappers

import io.photopixels.data.network.responses.MediaItemsResponse
import io.photopixels.data.network.responses.PhotoPickingSessionResponse
import io.photopixels.data.storage.database.entities.GooglePhotosEntity
import io.photopixels.domain.model.GooglePhoto
import io.photopixels.domain.model.MediaItems
import io.photopixels.domain.model.PhotoPickingSession

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

fun PhotoPickingSessionResponse.toDomain() = PhotoPickingSession(
    id = id,
    expireTime = expireTime,
    pickerUri = pickerUri.orEmpty(),
    mediaItemsSet = mediaItemsSet,
    pollInterval = pollingConfig?.pollInterval.orEmpty(),
)

fun MediaItemsResponse.toDomain() = MediaItems(
    mediaItems = mediaItems.mapNotNull { mediaItem ->
        mediaItem.takeIf { it.type == MediaItemsResponse.PickedMediaItem.Type.PHOTO }
            ?.let {
                GooglePhoto(
                    androidCloudId = it.id,
                    fileName = it.mediaFile.filename,
                    mimeType = it.mediaFile.mimeType,
                    baseUrl = it.mediaFile.baseUrl,
                    hash = null,
                    serverItemHashId = null,
                    isAlreadyUploaded = null
                )
            }
    },
    nextPageToken = nextPageToken
)
