package com.scalefocus.domain.utils

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import timber.log.Timber
import java.security.MessageDigest

/**
 * Helper class for generating hashes for photos.
 * SHA1 is used for full hash(all file)
 */
object Hasher {
    private const val SAMPLE_SIZE = 16 // Adjust based on desired sample coverage
    private const val SAMPLE_COUNT = 4 // Adjust based on desired number of samples

    // Generate fast hash
    fun generateFastHash(contentResolver: ContentResolver, contentUri: Uri): String? {
        val inputStream = contentResolver.openInputStream(contentUri) ?: return null
        var hashString: String?

        try {
            val bufferSize = SAMPLE_SIZE * SAMPLE_COUNT // Size for all samples combined
            val bytes = ByteArray(bufferSize)
            var totalBytesRead = 0

            for (i in 0 until SAMPLE_COUNT) {
                val skipAmount = inputStream.available() / (SAMPLE_COUNT + 1) // Skip to roughly even intervals
                inputStream.skip(skipAmount.toLong()) // Skip to desired sample position

                val bytesRead = inputStream.read(bytes, totalBytesRead, SAMPLE_SIZE)
                if (bytesRead == -1) {
                    break // Handle end of stream
                }
                totalBytesRead += bytesRead
            }

            hashString = if (totalBytesRead == 0) {
                "0".repeat(bufferSize * 2) // String of zeros if no bytes read (adjust formatting)
            } else {
                // Combine the read bytes into a String representation
                val stringBuilder = StringBuilder()
                for (i in 0 until totalBytesRead) {
                    stringBuilder.append("%02x".format(bytes[i].toInt()))
                }
                stringBuilder.toString()
            }
        } catch (e: Exception) {
            // Handle potential exceptions during file access or reading
            Timber.tag("TAG").e("Error while hashing file: $e")
            hashString = null
        } finally {
            inputStream.close()
        }

        return hashString
    }

    // Generate SHA-1 Hash for full file, which will be uploaded to BE
    fun sha1HashBase64(bytes: ByteArray): String {
        val messageDigest = MessageDigest.getInstance("SHA-1")
        messageDigest.update(bytes)
        val hashBytes = messageDigest.digest()
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }
}
