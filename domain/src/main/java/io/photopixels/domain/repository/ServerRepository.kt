package io.photopixels.domain.repository

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.model.ServerStatus
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    suspend fun getServerStatus(serverAddress: String): Flow<Response<ServerStatus>>

    suspend fun getServerRevision(specificRevision: Int?): Flow<Response<ServerRevision>>

    suspend fun setServerAddressToDataStore(serverAddress: String)

    suspend fun getServerAddress(): String?

    suspend fun setServerVersionToDataStore(serverVersion: String)

    suspend fun getServerVersion(): String

    suspend fun clearServerData()
}
