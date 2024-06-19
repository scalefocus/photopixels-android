package io.photopixels.data.network.requests

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

object UploadPhotoRequest {
    fun build(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        objectHash: String,
        androidCloudId: String
    ): MultiPartFormDataContent {
        // val fileFormat = getFileFormatFromMimeType(mimeType)
        val multipart = MultiPartFormDataContent(
            formData {
                append("objectHash", objectHash)
                append("AndroidCloudId", androidCloudId)
                append("AppleCloudId", "")

                append(
                    "file",
                    fileBytes,
                    Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        append(HttpHeaders.ContentLength, fileBytes.size)
                    }
                )
            },
            boundary = "WebAppBoundary"
        )

        return multipart
    }

//        private fun getFileFormatFromMimeType(mimeType: String): String {
//            return if (mimeType.contains("/")) {
//                mimeType.split("/")[1]
//            } else {
//                "jpg"
//            }
//        }
}
