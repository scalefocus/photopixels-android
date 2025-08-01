package io.photopixels.data.network

import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.http.HttpHeaders
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.domain.base.Response
import javax.inject.Inject

/**
 * This class is responsible for refreshing Photo Pixels auth tokens
 */
internal class Authenticator @Inject constructor(
    private val authApi: AuthApi,
    private val authDataStore: AuthDataStore,
) {

    suspend fun refreshToken(): BearerTokens {
        authDataStore.getRefreshToken()?.let {
            val response = authApi.refreshToken(it)
            if (response is Response.Success) {
                with(response.result) {
                    // update stored tokens
                    authDataStore.storeAuthHeaders(accessToken, refreshToken)
                    return BearerTokens(accessToken, refreshToken)
                }
            }
        }

        return BearerTokens("", "")
    }

    /**
     * returns stored [BearerTokens] tokens ready to be used by ktor client
     */
    suspend fun getBearerTokens() = BearerTokens(
        accessToken = authDataStore.getAuthToken().orEmpty(),
        refreshToken = authDataStore.getRefreshToken(),
    )

    /**
     * returns a (key, value) pair of authorization header and the bearer token
     */
    suspend fun getAuthHeader(): Pair<String, String>? =
        authDataStore.getAuthToken()?.let { HttpHeaders.Authorization to "Bearer $it" }
}
