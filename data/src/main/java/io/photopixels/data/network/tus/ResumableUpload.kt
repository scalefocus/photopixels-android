package io.photopixels.data.network.tus

import android.content.Context
import android.net.Uri
import io.tus.android.client.TusAndroidUpload

/**
 * Creates a TusUpload with required metadata from a Uri.
 */
internal fun Context.createResumableUpload(
    uri: Uri,
    fileName: String,
    objectHash: String,
) = TusAndroidUpload(uri, this).apply {
    metadata = mapOf(
        "fileExtension" to fileName.substringAfterLast('.', ""),
        "fileName" to fileName,
        "fileHash" to objectHash,
        "fileSize" to size.toString(),
        "appleId" to "",
        "androidId" to "",
    )
}
