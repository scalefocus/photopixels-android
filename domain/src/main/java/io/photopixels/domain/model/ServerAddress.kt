package io.photopixels.domain.model

import timber.log.Timber
import java.net.URL

data class ServerAddress(
    val protocol: String = HTTPS_PROTOCOL,
    val host: String = "",
    val port: Int? = null,
    val path: String? = null,
) {
    override fun toString(): String {
        val formattedPath = path?.let { "/$path" }.orEmpty()
        val formattedPort = port?.let { ":$port" }.orEmpty()
        return "$protocol://$host$formattedPort$formattedPath"
    }

    companion object {
        private const val HTTP_PROTOCOL = "http"
        private const val HTTPS_PROTOCOL = "https"

        /**
         * Parses server address from a string
         */
        fun fromString(urlString: String): ServerAddress {
            val isUrlStartsWithProtocol = listOf(HTTPS_PROTOCOL, HTTP_PROTOCOL)
                .any { urlString.startsWith("$it://", ignoreCase = true) }

            val urlWithProtocol = if (isUrlStartsWithProtocol) urlString else "$HTTPS_PROTOCOL://$urlString"

            return runCatching {
                val url = URL(urlWithProtocol)
                val serverAddress = ServerAddress(
                    protocol = url.protocol,
                    host = url.host,
                    port = url.port.takeIf { it > 0 },
                    path = url.path.trim('/').takeIf { it.isNotBlank() },
                )
                Timber.tag("TAG").d("ServerAddress:$serverAddress")
                serverAddress
            }.getOrElse { ServerAddress() }
        }
    }
}
