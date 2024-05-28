package com.scalefocus.data.mappers

import com.scalefocus.data.network.responses.ServerRevisionResponse
import com.scalefocus.data.network.responses.ServerStatusResponse
import com.scalefocus.domain.model.ServerRevision
import com.scalefocus.domain.model.ServerStatus

fun ServerStatusResponse.toDomain() = ServerStatus(registration, serverVersion)

fun ServerRevisionResponse.toDomain() = ServerRevision(added = added, deleted = deleted, version = version)
