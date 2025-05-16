package io.photopixels.domain.extensions

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

private const val READ_BUFFER_SIZE = 1024

suspend fun ContentResolver.readFileContent(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
    openInputStream(uri)?.use { inputStream ->
        val buffer = ByteArrayOutputStream()
        val data = ByteArray(READ_BUFFER_SIZE)
        var nRead: Int
        while (inputStream.read(data).also { nRead = it } != -1) {
            buffer.write(data, 0, nRead)
        }
        buffer.flush()
        buffer.toByteArray()
    }
}
