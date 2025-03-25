package io.photopixels.domain.base

interface GoogleAuth {
    suspend fun performRefreshTokenRequest(): String?

    fun getGoogleAuthToken(): String?
}
