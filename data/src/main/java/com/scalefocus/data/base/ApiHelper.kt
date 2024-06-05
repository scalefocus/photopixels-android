package com.scalefocus.data.base

import com.scalefocus.domain.base.PhotoPixelError
import com.scalefocus.domain.base.Response
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import timber.log.Timber
import java.io.IOException

/**
 * Use this as wrapper when making network request in the repository
 *
 * Usage:
 * request {
 *             httpClient.post {
 *                 url("someUrl")
 *                 contentType(ContentType.Application.Json)
 *                 setBody(body)
 *             }.body<ResponseObject> ()
 *
 *             Resource.Success(true)
 *         }
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <T> request(block: suspend () -> Response<T>): Response<T> {
    return try {
        block.invoke()
    } catch (t: Throwable) {
        Timber.tag(HTTP_ERROR_TAG).e(t)
        handleThrowableResponse(t)
    }
}

suspend fun <T> handleThrowableResponse(throwable: Throwable): Response<T> {
    return when (throwable) {
        is ResponseException -> throwable.response.body<HttpResponse>().let {
            Response.Failure(translateServerError(it))
        }

        is IOException,
        is InterruptedException -> {
            // Irrelevant network problem or API that throws on cancellation or
            // blocking code was interrupted - do not record an exception
            Response.Failure(PhotoPixelError.NoInternetConnection)
        }

        else -> Response.Failure(PhotoPixelError.GenericError)
    }
}

// TODO: Add more BE errors here
private suspend fun translateServerError(httpResponse: HttpResponse): PhotoPixelError {
    val bodyText = httpResponse.bodyAsText()
    return when (httpResponse.status.value) {
        HttpStatusCode.Conflict.value, CUSTOM_BE_ERROR -> { // Duplicate photo error while uploading
            PhotoPixelError.DuplicatePhotoError
        }

        HttpStatusCode.BadRequest.value -> {
            var error: PhotoPixelError = PhotoPixelError.GenericError
            if (bodyText.contains(PhotoPixelsErrorResponses.INCORRECT_VERIFICATION_CODE)) {
                error = PhotoPixelError.VerificationCodeIncorrect
            } else if (bodyText.contains(PhotoPixelsErrorResponses.EMAIL_ALREADY_TAKEN)) {
                error = PhotoPixelError.AccountAlreadyTaken
            }
            error
        }

        else -> {
            PhotoPixelError.ServerError
        }
    }
}

private const val CUSTOM_BE_ERROR = -1
private const val HTTP_ERROR_TAG = "network_error"
