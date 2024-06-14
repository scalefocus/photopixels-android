package io.photopixels.data.base

/**
 * Define error responses(text-plain) for PhotoPixels API
 *
 * Note: BE currently doesn't have unified error Response object when error occurs(business or not), instead of that
 * text plain is used as error message.
 * When it's fixed define the BE error codes here, and map them to [io.photopixels.domain.base.PhotoPixelError]
 */
internal object PhotoPixelsErrorResponses {
    const val INCORRECT_VERIFICATION_CODE = "The provided code is invalid"
    const val EMAIL_ALREADY_TAKEN = "DuplicateUserName"
}
