package io.photopixels.data.repository

import io.photopixels.data.network.BackendApi
import io.photopixels.data.storage.datastore.UserPreferencesDataStore
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerAddress
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
    override suspend fun getServerStatus(serverAddress: ServerAddress): Flow<Response<ServerStatus>> =
        flow {
            val result = backendApi.getServerStatus(serverAddress)
            emit(result)
        }

    override suspend fun getServerRevision(specificRevision: Int): Response<ServerRevision> =
        backendApi.getServerRevision(specificRevision)

    override suspend fun setServerAddressToDataStore(serverAddress: ServerAddress) {
        userPreferencesDataStore.setServerAddress(serverAddress)
    }

    override suspend fun setServerVersionToDataStore(serverVersion: String) {
        userPreferencesDataStore.setServerVersion(serverVersion)
    }

    override suspend fun getServerVersion(): String = userPreferencesDataStore.getServerVersion()

    override suspend fun clearServerData() {
        userPreferencesDataStore.clearServerData()
    }

    override suspend fun getServerAddress(): ServerAddress? = userPreferencesDataStore.getServerAddress()

    override suspend fun setLocalRevision(revision: Int) {
        userPreferencesDataStore.setLocalRevision(revision)
    }

    override suspend fun getLocalRevision(): Int = userPreferencesDataStore.getLocalRevision()

    override suspend fun clearLocalRevision() = userPreferencesDataStore.clearLocalRevision()
}
