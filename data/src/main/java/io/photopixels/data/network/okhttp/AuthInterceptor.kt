package io.photopixels.data.network.okhttp

import io.photopixels.data.network.AuthApi
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.domain.base.Response
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import java.net.HttpURLConnection
import javax.inject.Inject
import okhttp3.Response as OkHttpResponse

class AuthInterceptor @Inject constructor(
    private val authApi: AuthApi,
    private val authDataStore: AuthDataStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): OkHttpResponse {
        val authToken = runBlocking { authDataStore.getAuthToken() }

        val request = newRequestWithAccessToken(chain.request(), authToken.orEmpty())
        val response = chain.proceed(request)

        return if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            synchronized(this) {
                runBlocking {
                    val refreshToken = authDataStore.getRefreshToken().orEmpty()
                    val authResponse = authApi.refreshToken(refreshToken)
                    if (authResponse is Response.Success) {
                        val newAuthToken = authResponse.result.accessToken
                        val newRefreshToken = authResponse.result.refreshToken

                        authDataStore.storeAuthHeaders(authHeader = newAuthToken, refreshToken = newRefreshToken)
                        val newRequest = newRequestWithAccessToken(request, newAuthToken)
                        chain.proceed(newRequest)
                    } else {
                        response
                    }
                }
            }
        } else {
            response
        }
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}
