package io.photopixels.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ServerStatusResponse(val registration: Boolean, val serverVersion: String)
