package com.scalefocus.domain.model

data class ServerRevision(
    val added: Map<String, Long>,
    val version: Int,
    val deleted: List<String>?
)
