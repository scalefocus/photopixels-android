package io.photopixels.data.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.photopixels.data.network.BackendApi
import io.photopixels.data.network.BackendApiImpl
import io.photopixels.data.network.responses.LoginResponse
import io.photopixels.data.network.responses.RefreshTokenRequest
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.data.storage.datastore.UserPreferencesDataStore
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    companion object {
        private const val KTOR_REFRESH_LOGGER_TAG = "ktor_refresh_logger"
        private const val KTOR_LOGGER_TAG = "ktor_logger"
        private const val KTOR_HTTP_STATUS_TAG_ = "http_status"
        private const val KTOR_REQUEST_TIMEOUT = 30L * 1000 // seconds
    }

    @Provides
    @Singleton
    @Named("refreshTokenHttpClient")
    fun provideRefreshTokenHttpClient(userPreferencesDataStore: UserPreferencesDataStore): HttpClient {
        val httpClient = HttpClient(Android) {
            expectSuccess = true
            install(Auth) {
                bearer {
                    sendWithoutRequest { true } // Don't send token for token refresh request
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                url {
                    runBlocking {
                        userPreferencesDataStore.getServerAddress()?.let {
                            host = it.host
                            protocol = URLProtocol.createOrDefault(it.protocol)
                        }
                    }
                }
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v(KTOR_REFRESH_LOGGER_TAG, message)
                    }
                }
                level = LogLevel.ALL
            }
        }
        return httpClient
    }

    @Suppress("LongMethod")
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideHttpClient(
        authDataStore: AuthDataStore,
        userDataStore: UserPreferencesDataStore,
        @Named("refreshTokenHttpClient") refreshTokenHttpClient: HttpClient
    ): HttpClient = HttpClient(Android) {
        expectSuccess = true

        install(HttpTimeout) {
            requestTimeoutMillis = KTOR_REQUEST_TIMEOUT
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v(KTOR_LOGGER_TAG, message)
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }

        install(Auth) {
            bearer {
                sendWithoutRequest { true } // Don't send token for token refresh request
                loadTokens {
                    val tokenPair = Pair(authDataStore.getAuthToken(), authDataStore.getRefreshToken())
                    val authToken = tokenPair.first
                    val refreshToken = tokenPair.second
                    authToken?.let {
                        refreshToken?.let { refreshToken ->
                            BearerTokens(authToken, refreshToken) // Use stored access token
                        }
                    }
                }
                refreshTokens {
                    return@refreshTokens runBlocking {
                        val storedRefreshToken = authDataStore.getRefreshToken()

                        storedRefreshToken?.let {
                            val newTokenPair = doRefreshToken(
                                refreshToken = storedRefreshToken,
                                authDataStore = authDataStore,
                                httpClient = refreshTokenHttpClient
                            )
                            newTokenPair?.let {
                                BearerTokens(it.first, it.second)
                            } ?: run {
                                BearerTokens("", "")
                            }
                        }
                    }
                }
            }
        }

        install(ResponseObserver) {
            onResponse { response ->
                Log.d(KTOR_HTTP_STATUS_TAG_, "${response.status.value}")
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            runBlocking {
                val storedServerAddress = userDataStore.getServerAddress()
                storedServerAddress?.let { serverAddress ->
                    url {
                        host = serverAddress.host
                        protocol = URLProtocol.createOrDefault(serverAddress.protocol)
                        port = serverAddress.port
                    }
                }
            }
        }

        install(HttpRedirect) {
            checkHttpMethod = false
        }
    }

    @Singleton
    @Provides
    fun provideBackendApi(httpClient: HttpClient): BackendApi = BackendApiImpl(httpClient)

    private suspend fun doRefreshToken(
        refreshToken: String,
        @Named("refreshTokenHttpClient") httpClient: HttpClient,
        authDataStore: AuthDataStore,
    ): Pair<String, String>? = try {
        val response = httpClient
            .post {
                url("/api/user/refresh")
                setBody(RefreshTokenRequest(refreshToken = refreshToken))
            }.body<LoginResponse>()
        val newAuthToken = response.accessToken
        val newRefreshToken = response.refreshToken

        Log.e("TAG", "Refresh token call successful!!!!")
        authDataStore.storeAuthHeaders(newAuthToken, newRefreshToken)
        Pair(newAuthToken, newRefreshToken)
    } catch (exception: ResponseException) {
        Log.e("TAG", "Error Refresh token call:$exception!!!!")
        null
    }
}
