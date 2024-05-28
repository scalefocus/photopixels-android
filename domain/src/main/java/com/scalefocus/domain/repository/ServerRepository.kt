package com.scalefocus.domain.repository

import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.ServerRevision
import com.scalefocus.domain.model.ServerStatus
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
