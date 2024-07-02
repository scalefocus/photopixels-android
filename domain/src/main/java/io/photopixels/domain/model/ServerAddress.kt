package io.photopixels.domain.model

import io.photopixels.domain.utils.Utils.HTTPS_PORT
import io.photopixels.domain.utils.Utils.HTTPS_PROTOCOL

data class ServerAddress(
    val protocol: String = HTTPS_PROTOCOL,
    val host: String = "",
    val port: Int = HTTPS_PORT
) {
    override fun toString(): String = "$protocol://$host:$port"
}
