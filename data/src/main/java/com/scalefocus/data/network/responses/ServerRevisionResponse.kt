package com.scalefocus.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ServerRevisionResponse(
    val id: String,
    val version: Int,
    val added: Map<String, Long>,
    val deleted: List<String>?
)
