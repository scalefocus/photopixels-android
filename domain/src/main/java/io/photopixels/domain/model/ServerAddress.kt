package io.photopixels.domain.model

import timber.log.Timber
import java.net.URL

data class ServerAddress(
    val protocol: String = HTTPS_PROTOCOL,
    val host: String = "",
    val port: Int = DEFAULT_PORT,
    val path: String = "",
) {
    override fun toString(): String {
        val pathWithDivider = if (path.isEmpty()) "" else "/$path"
        val portWithDivider = if (port > 0) ":$port" else ""
        return "$protocol://$host$portWithDivider$pathWithDivider"
    }

    companion object {
        private const val HTTP_PROTOCOL = "http"
        private const val HTTPS_PROTOCOL = "https"
        private const val DEFAULT_PORT = 0

        /**
         * Parses server address from a string
         */
        fun fromString(urlString: String): ServerAddress {
            val isUrlStartsWithProtocol = listOf(HTTPS_PROTOCOL, HTTP_PROTOCOL)
                .any { urlString.startsWith("$it://", ignoreCase = true) }

            val urlWithProtocol = if (isUrlStartsWithProtocol) urlString else "$HTTPS_PROTOCOL://$urlString"

            return runCatching {
                val url = URL(urlWithProtocol)
                val port = url.port.takeIf { it > 0 } ?: DEFAULT_PORT
                val path = url.path.trim('/')
                val serverAddress = ServerAddress(url.protocol, url.host, port, path)
                Timber.tag("TAG").d("ServerAddress:$serverAddress")
                serverAddress
            }.getOrElse { ServerAddress() }
        }
    }
}
