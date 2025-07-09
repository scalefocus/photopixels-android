package io.photopixels.domain.model

import android.net.Uri
import android.provider.MediaStore

enum class MediaType(val mediaUri: Uri) {
    IMAGE(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
    VIDEO(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
}
