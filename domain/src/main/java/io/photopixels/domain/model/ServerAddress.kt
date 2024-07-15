package io.photopixels.domain.model

import timber.log.Timber

data class ServerAddress(
    val protocol: String = HTTPS_PROTOCOL,
    val host: String = "",
    val port: Int = HTTPS_PORT
) {
    override fun toString(): String = "$protocol://$host:$port"

    companion object {
        const val HTTP_PROTOCOL = "http"
        const val HTTP_PORT = 80
        const val HTTPS_PROTOCOL = "https"
        const val HTTPS_PORT = 443

        fun fromString(urlString: String): ServerAddress {
            val parts = urlString.split("://", limit = 2)

            val protocol = if (parts.size == 2) parts[0].lowercase() else HTTPS_PROTOCOL // Protocol if present
            val remaining = parts[parts.lastIndex] // Last part (host or protocol+host)

            // Split remaining part by colon (separates host and potential port)
            val hostAndPort = remaining.split(":")

            // Extract host (must exist)
            val host = hostAndPort[0]

            // Check for port (optional)
            val portString = if (hostAndPort.size > 1) hostAndPort[1] else null

            // Convert port to integer (default 80/443 if invalid)
            val port = portString?.toIntOrNull() ?: kotlin.run {
                if (protocol == HTTP_PROTOCOL) HTTP_PORT else HTTPS_PORT
            }

            val serverAddress = ServerAddress(protocol, host, port)
            Timber.tag("TAG").d("ServerAddress:$serverAddress")
            return serverAddress
        }
    }
}
