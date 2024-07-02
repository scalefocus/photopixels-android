package io.photopixels.domain.model

data class ServerAddress(
    val protocol: String = "https",
    val host: String = "",
    val port: Int = 443
) {
    override fun toString(): String = "$protocol://$host:$port"
}
