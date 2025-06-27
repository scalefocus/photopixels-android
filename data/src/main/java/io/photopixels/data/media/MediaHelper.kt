package io.photopixels.data.media

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.domain.model.DeviceMedia
import io.photopixels.domain.utils.DateHelper
import javax.inject.Inject

class MediaHelper @Inject constructor(private val uri: Uri) {

    fun scanPhotos(
        @ApplicationContext context: Context
    ): List<DeviceMedia> {
        val photosData = mutableListOf<DeviceMedia>()

        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(
            uri,
            arrayOf(
                MediaStore.Images.Media._ID, // MediaStore ID
                MediaStore.Images.Media.DISPLAY_NAME, // Filename
                MediaStore.Images.Media.SIZE, // Filesize
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED, // Date the media was added to the device
                MediaStore.Images.Media.DATE_TAKEN, // dateTimeOriginal from Exif if present
            ),
            null, // No selection criteria
            null, // No selection arguments
            MediaStore.Images.Media.DATE_ADDED + " DESC" // Order by date added descending
        ) ?: return emptyList()

        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val filename = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                val dateCreated = cursor.getDateCreated()
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                photosData.add(
                    DeviceMedia(
                        id = id.toString(),
                        fileName = filename,
                        fileSize = fileSize,
                        mimeType = mimeType,
                        contentUri = contentUri.toString(),
                        dateCreated = dateCreated,
                    )
                ) // Use toString() for string representation
            }
        }

        return photosData
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
