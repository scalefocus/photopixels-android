package io.photopixels.domain.base

sealed class PhotoPixelError {
    data object ServerError : PhotoPixelError()

    data object GenericError : PhotoPixelError()

    data object DuplicatePhotoError : PhotoPixelError()

    data object InvalidUsernameOrPassword : PhotoPixelError()

    data object NoInternetConnection : PhotoPixelError()

    data object VerificationCodeIncorrect : PhotoPixelError()

    data object AccountAlreadyTaken : PhotoPixelError()

    data object ExpiredGoogleAuthTokenError : PhotoPixelError()

    data object GenericGoogleError : PhotoPixelError()
}
