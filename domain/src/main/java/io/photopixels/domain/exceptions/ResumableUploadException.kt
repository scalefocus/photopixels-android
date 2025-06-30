package io.photopixels.domain.exceptions

import io.photopixels.domain.base.PhotoPixelError

class ResumableUploadException(val error: PhotoPixelError, cause: Throwable) : Exception(cause)
