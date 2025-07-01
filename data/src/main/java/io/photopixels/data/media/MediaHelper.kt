package io.photopixels.data.media

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.model.MediaType
import io.photopixels.domain.utils.DateHelper
import javax.inject.Inject

class MediaHelper @Inject constructor(private val uri: Uri) {

    fun scanDeviceMedia(appContext: Context): List<DeviceMedia> {
        val contentResolver = appContext.contentResolver
        val images = contentResolver.scanMedia(
            query = uri,
            mediaType = MediaType.IMAGE,
            externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        val videos = contentResolver.scanMedia(
            query = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            mediaType = MediaType.VIDEO,
            externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )

        return images.plus(videos)
            .sortedByDescending { it.dateCreated }
    }

    private fun ContentResolver.scanMedia(
        query: Uri,
        mediaType: MediaType,
        externalContentUri: Uri
    ): List<DeviceMedia> {
        val videoItems = mutableListOf<DeviceMedia>()

        val cursor = query(
            query,
            arrayOf(
                MediaStore.MediaColumns._ID, // MediaStore ID
                MediaStore.MediaColumns.DISPLAY_NAME, // Filename
                MediaStore.MediaColumns.SIZE, // Filesize
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DATE_ADDED, // Date the media was added to the device
                MediaStore.MediaColumns.DATE_TAKEN, // dateTimeOriginal from Exif if present
            ),
            null, // No selection criteria
            null, // No selection arguments
            "${MediaStore.MediaColumns.DATE_ADDED} DESC" // Order by date added descending
        ) ?: return emptyList()

        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val filename = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                val dateCreated = cursor.getDateCreated()
                val contentUri = ContentUris.withAppendedId(externalContentUri, id)

                videoItems.add(
                    DeviceMedia(
                        id = id,
                        fileName = filename,
                        fileSize = fileSize,
                        mediaType = mediaType,
                        mimeType = mimeType,
                        contentUri = contentUri.toString(),
                        dateCreated = dateCreated,
                    )
                ) // Use toString() for string representation
            }
        }

        return videoItems
    }

    private fun Cursor.getDateCreated(): String {
        val dateTakenEpochMilli = getLong(getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN))
        val dateAddedEpochSeconds = getLong(getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))

        return if (dateTakenEpochMilli > 0) {
            DateHelper.epochMilliToDateTimeString(dateTakenEpochMilli)
        } else {
            DateHelper.epochSecondsToDateTimeString(dateAddedEpochSeconds)
        }
    }
}
