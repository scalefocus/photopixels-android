package io.photopixels.data.network.okhttp

import io.photopixels.data.network.Authenticator
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import java.net.HttpURLConnection
import javax.inject.Inject
import okhttp3.Response as OkHttpResponse

/**
 * An OkHttp [Interceptor] that handles authentication for API requests made by Exoplayer
 */
internal class AuthInterceptor @Inject constructor(
    private val authenticator: Authenticator,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): OkHttpResponse {
        val request = newRequestWithAccessToken(chain.request())
        val response = chain.proceed(request)

        return if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // auth token expired, try to refresh it
            synchronized(this) {
                runBlocking {
                    val tokenResponse = authenticator.refreshToken()
                    if (tokenResponse.accessToken.isNotBlank()) {
                        // retry the request with the new token
                        val newRequest = newRequestWithAccessToken(request)
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

    /**
     * Creates a new request with the access token in the Authorization header.
     */
    private fun newRequestWithAccessToken(request: Request): Request = with(request.newBuilder()) {
        runBlocking {
            authenticator.getAuthHeader()?.let { (headerName, headerValue) ->
                // apply auth header
                header(headerName, headerValue)
            }
        }

        build()
    }
}
