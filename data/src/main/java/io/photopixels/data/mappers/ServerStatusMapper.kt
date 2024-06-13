package io.photopixels.data.mappers

import io.photopixels.data.network.responses.ServerRevisionResponse
import io.photopixels.data.network.responses.ServerStatusResponse
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.model.ServerStatus

fun ServerStatusResponse.toDomain() = ServerStatus(registration, serverVersion)

fun ServerRevisionResponse.toDomain() = ServerRevision(added = added, deleted = deleted, version = version)
