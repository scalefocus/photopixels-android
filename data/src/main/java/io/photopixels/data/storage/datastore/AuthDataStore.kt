package io.photopixels.data.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Android Data store for write and read authentication headers with encryption.
 */
class AuthDataStore @Inject constructor(
    private val context: Context,
    private val cipherUtil: CipherUtil
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

    suspend fun storeUsername(username: String) {
        val encryptedUsername = cipherUtil.encrypt(username)

        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = encryptedUsername
        }
    }

    suspend fun getUsername(): String? {
        val encryptedUsername = context.dataStore.data.first()[USERNAME_KEY] ?: return null
        return cipherUtil.decrypt(encryptedUsername)
    }

    suspend fun storeAuthHeaders(authHeader: String, refreshToken: String) {
        val encryptedAuthToken = cipherUtil.encrypt(authHeader)
        val encryptedRefreshToken = cipherUtil.encrypt(refreshToken)

        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = encryptedAuthToken
            preferences[REFRESH_TOKEN_KEY] = encryptedRefreshToken
        }
    }

    suspend fun getAuthToken(): String? {
        val encryptedAuthToken = context.dataStore.data.first()[AUTH_TOKEN_KEY] ?: return null
        return cipherUtil.decrypt(encryptedAuthToken)
    }

    suspend fun getRefreshToken(): String? {
        val encryptedRefreshToken = context.dataStore.data.first()[REFRESH_TOKEN_KEY] ?: return null
        return cipherUtil.decrypt(encryptedRefreshToken)
    }

    suspend fun clearUserData() {
        context.dataStore.edit {
            it.clear()
        }
    }

    suspend fun storeGoogleAuthToken(googleAuthToken: String) {
        val encryptedAuthToken = cipherUtil.encrypt(googleAuthToken)

        context.dataStore.edit { preferences ->
            preferences[GOOGLE_AUTH_TOKEN_KEY] = encryptedAuthToken
        }
    }

    suspend fun getGoogleAuthToken(): String? {
        val encryptedGoogleAuthToken = context.dataStore.data.first()[GOOGLE_AUTH_TOKEN_KEY] ?: return null
        return cipherUtil.decrypt(encryptedGoogleAuthToken)
    }

    suspend fun clearGoogleAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(GOOGLE_AUTH_TOKEN_KEY)
        }
    }

    suspend fun storeGoogleAuthState(authState: String) {
        val encryptedAuthState = cipherUtil.encrypt(authState)

        context.dataStore.edit { preferences ->
            preferences[GOOGLE_AUTH_STATE_JSON_KEY] = encryptedAuthState
        }
    }

    suspend fun getGoogleAuthState(): String? {
        val encryptedGoogleAuthState = context.dataStore.data.first()[GOOGLE_AUTH_STATE_JSON_KEY] ?: return null
        return cipherUtil.decrypt(encryptedGoogleAuthState)
    }

    suspend fun clearGoogleAuthState() {
        context.dataStore.edit { preferences ->
            if (preferences.contains(GOOGLE_AUTH_STATE_JSON_KEY)) {
                preferences.remove(GOOGLE_AUTH_STATE_JSON_KEY)
            }
        }
    }

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val GOOGLE_AUTH_TOKEN_KEY = stringPreferencesKey("google_auth_token")
        private val GOOGLE_AUTH_STATE_JSON_KEY = stringPreferencesKey("google_auth_state")
    }
}
