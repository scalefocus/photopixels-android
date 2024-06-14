package io.photopixels.data.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserPreferencesDataStore @Inject constructor(private val context: Context, private val cipherUtil: CipherUtil) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    companion object {
        private val SERVER_ADDRESS_KEY = stringPreferencesKey("server_address")
        private val SERVER_VERSION_KEY = stringPreferencesKey("server_version")
    }

    suspend fun setServerAddress(serverAddress: String) {
        val encryptedServerAddress = cipherUtil.encrypt(serverAddress)

        context.dataStore.edit { preferences ->
            preferences[SERVER_ADDRESS_KEY] = encryptedServerAddress
        }
    }

    suspend fun getServerAddress(): String? {
        val encryptedServerAddress = context.dataStore.data.first()[SERVER_ADDRESS_KEY] ?: return null
        return cipherUtil.decrypt(encryptedServerAddress)
    }

    suspend fun setServerVersion(serverVersion: String) {
        val encryptedServerVersion = cipherUtil.encrypt(serverVersion)

        context.dataStore.edit { preferences ->
            preferences[SERVER_VERSION_KEY] = encryptedServerVersion
        }
    }

    suspend fun getServerVersion(): String {
        val encryptedServerVersion = context.dataStore.data.first()[SERVER_VERSION_KEY] ?: return ""
        return cipherUtil.decrypt(encryptedServerVersion)
    }

    suspend fun clearServerData() {
        context.dataStore.edit { it.clear() }
    }
}
