package com.scalefocus.domain.base

sealed class Response<out T> {
    data class Success<out T>(val result: T) : Response<T>()

    data class Failure(val error: PhotoPixelError, val details: Any? = null) : Response<Nothing>()
}
