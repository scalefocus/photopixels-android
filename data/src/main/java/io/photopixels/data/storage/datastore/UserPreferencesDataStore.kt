package io.photopixels.data.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import io.photopixels.domain.model.ServerAddress
import io.photopixels.domain.model.UserSettings
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserPreferencesDataStore @Inject constructor(
    private val context: Context,
    private val cipherUtil: CipherUtil
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    companion object {
        private val SERVER_ADDRESS_KEY = stringPreferencesKey("server_address")
        private val SERVER_VERSION_KEY = stringPreferencesKey("server_version")
        private val USER_SETTINGS_KEY = stringPreferencesKey("user_settings")
    }

    suspend fun setServerAddress(serverAddress: ServerAddress) {
        val encryptedServerAddress = cipherUtil.encrypt(Gson().toJson(serverAddress))

        context.dataStore.edit { preferences ->
            preferences[SERVER_ADDRESS_KEY] = encryptedServerAddress
        }
    }

    suspend fun getServerAddress(): ServerAddress? {
        val encryptedServerAddressJson = context.dataStore.data.first()[SERVER_ADDRESS_KEY] ?: return null
        return Gson().fromJson(cipherUtil.decrypt(encryptedServerAddressJson), ServerAddress::class.java)
        // TODO: Gson can be replaced with kotlin-serialization
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

    suspend fun setUserSettings(userSettings: UserSettings) {
        val jsonString = Gson().toJson(userSettings)

        context.dataStore.edit { preferences ->
            preferences[USER_SETTINGS_KEY] = jsonString
        }
    }

    @Suppress("SwallowedException")
    suspend fun getUserSettings(): UserSettings? = try {
        val jsonString = context.dataStore.data.first()[USER_SETTINGS_KEY]
        Gson().fromJson(jsonString, UserSettings::class.java)
    } catch (ex: NoSuchElementException) {
        null
    }
}
