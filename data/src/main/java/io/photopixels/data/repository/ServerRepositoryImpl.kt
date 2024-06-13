package io.photopixels.data.repository

import io.photopixels.data.network.BackendApi
import io.photopixels.data.storage.datastore.UserPreferencesDataStore
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.model.ServerStatus
import io.photopixels.domain.repository.ServerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ServerRepository {
    override suspend fun getServerStatus(serverAddress: String): Flow<Response<ServerStatus>> =
        flow {
            val result = backendApi.getServerStatus(serverAddress)
            emit(result)
        }

    override suspend fun getServerRevision(specificRevision: Int?): Flow<Response<ServerRevision>> =
        flow {
            val result = backendApi.getServerRevision()
            emit(result)
        }

    override suspend fun setServerAddressToDataStore(serverAddress: String) {
        userPreferencesDataStore.setServerAddress(serverAddress)
    }

    override suspend fun setServerVersionToDataStore(serverVersion: String) {
        userPreferencesDataStore.setServerVersion(serverVersion)
    }

    override suspend fun getServerVersion(): String {
        return userPreferencesDataStore.getServerVersion()
    }

    override suspend fun clearServerData() {
        userPreferencesDataStore.clearServerData()
    }

    override suspend fun getServerAddress(): String? {
        return userPreferencesDataStore.getServerAddress()
    }
}
