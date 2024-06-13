package io.photopixels.data.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.utils.Hasher

object MediaHelper {

    fun scanPhotosAndGenerateHashes(
        @ApplicationContext context: Context
    ): List<PhotoData> {
        val photosData = mutableListOf<PhotoData>()

        val contentResolver = context.contentResolver

        val query = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() // Adjust for filtering if needed
        val cursor = contentResolver.query(
            Uri.parse(query),
            arrayOf(
                MediaStore.Images.Media._ID, // MediaStore ID
                MediaStore.Images.Media.DISPLAY_NAME, // Filename
                MediaStore.Images.Media.SIZE, // Filesize
                MediaStore.Images.Media.MIME_TYPE
            ),
            null, // No selection criteria
            null, // No selection arguments
            MediaStore.Images.Media.DATE_ADDED + " DESC" // Order by date added descending
        ) ?: return emptyList()

        try {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val filename = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val fastHash = Hasher.generateFastHash(contentResolver, contentUri) ?: ""

                photosData.add(
                    PhotoData(
                        id = id.toString(),
                        fileName = filename,
                        fileSize = fileSize,
                        mimeType = mimeType,
                        androidCloudId = fastHash,
                        contentUri = contentUri.toString()
                    )
                ) // Use toString() for string representation
            }
        } finally {
            cursor.close()
        }

        return photosData
    }
}
