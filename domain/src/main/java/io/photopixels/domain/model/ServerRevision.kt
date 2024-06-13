package io.photopixels.domain.model

data class ServerRevision(
    val added: Map<String, Long>,
    val version: Int,
    val deleted: List<String>?
)
