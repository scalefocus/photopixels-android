package io.photopixels.data.media

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.domain.model.PhotoData
import io.photopixels.domain.utils.Hasher
import java.io.ByteArrayOutputStream

private const val THUMB_SIZE = 200
private const val THUMB_QUALITY = 75

object MediaHelper {

    private val thumbSize = Size(THUMB_SIZE, THUMB_SIZE)

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
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED
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
                        id = id,
                        fileName = filename,
                        fileSize = fileSize,
                        mimeType = mimeType,
                        androidCloudId = fastHash,
                        contentUri = contentUri.toString(),
                        dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)),
                    )
                ) // Use toString() for string representation
            }
        } finally {
            cursor.close()
        }

        return photosData
    }

    fun loadPhotoThumbnail(context: Context, photo: PhotoData): ByteArray {
        return loadThumbnail(context.contentResolver, Uri.parse(photo.contentUri), photo.id).compressToJpeg()
    }

    private fun Bitmap.compressToJpeg(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, THUMB_QUALITY, outputStream)
        return outputStream.toByteArray()
    }

    private fun loadThumbnail(contentResolver: ContentResolver, contentUri: Uri, imageId: Long): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.loadThumbnail(contentUri, thumbSize, null)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Thumbnails.getThumbnail(
                contentResolver,
                imageId,
                MediaStore.Images.Thumbnails.MICRO_KIND,
                null,
            )
        }
    }
}
