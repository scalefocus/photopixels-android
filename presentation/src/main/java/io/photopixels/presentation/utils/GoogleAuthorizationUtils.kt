package io.photopixels.presentation.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

internal object GoogleAuthorizationUtils {
    private const val CODE_VERIFIER_SIZE = 64

    fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(CODE_VERIFIER_SIZE)
        secureRandom.nextBytes(bytes)

        val encoding = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        val codeVerifier = Base64.encodeToString(bytes, encoding)

        return codeVerifier
    }

    fun generateCodeChallenge(codeVerifier: String, digestAlgorithm: String): String {
        val digest = MessageDigest.getInstance(digestAlgorithm)
        val hash = digest.digest(codeVerifier.toByteArray())
        val encoding = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        val codeChallenge = Base64.encodeToString(hash, encoding)

        return codeChallenge
    }
}
