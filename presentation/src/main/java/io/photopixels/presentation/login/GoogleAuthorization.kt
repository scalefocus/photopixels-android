package io.photopixels.presentation.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.auth0.android.jwt.JWT
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.presentation.BuildConfig
import io.photopixels.presentation.utils.GoogleAuthorizationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import timber.log.Timber
import javax.inject.Inject

/**
 * This class is used to do user Authorization via appAuth library
 * Note: This class should be used if application needs user's permissions for accessing his data related to some Google APIs(drive, photos and etc..)
 */
class GoogleAuthorization @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private lateinit var authorizationService: AuthorizationService
    private var authState: AuthState = AuthState()
    private var jwt: JWT? = null
    private lateinit var authServiceConfig: AuthorizationServiceConfiguration

    // Google auth token for accessing Google APIs
    private val googleAuthTokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    init {
        // AppAuth library initialization
        initAuthServiceConfig()
        initAuthService()
    }

    // TODO: Handle expire token case
    // TODO: Handle auto-authorization

    fun generateAuthorizationIntent(): Intent? {
        if (CLIENT_ID.isBlank()) {
            Timber.tag(TAG).d("Google Client ID cannot be blank. Check Google OAuth configuration.")
            return null
        }

        val codeVerifier = GoogleAuthorizationUtils.generateCodeVerifier()
        val codeChallenge = GoogleAuthorizationUtils.generateCodeChallenge(
            codeVerifier,
            MESSAGE_DIGEST_ALGORITHM
        )

        val builder = AuthorizationRequest.Builder(
            authServiceConfig,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(REDIRECT_URI)
        )
            .setCodeVerifier(
                codeVerifier,
                codeChallenge,
                CODE_VERIFIER_CHALLENGE_METHOD
            )

        builder.setScopes(SCOPES)

        val request = builder.build()

        Timber.tag(TAG).e("Generated Authorization request:$request")
        return authorizationService.getAuthorizationRequestIntent(request)
    }

    fun handleAuthorizationResponse(intent: Intent): StateFlow<String?> {
        var oAuthToken: String? = null
        val authorizationResponse: AuthorizationResponse? = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        authState = AuthState(authorizationResponse, error)

        authorizationResponse?.let {
            // Exchange authorization code for authentication token
            val tokenExchangeRequest = authorizationResponse.createTokenExchangeRequest()
            authorizationService.performTokenRequest(tokenExchangeRequest) { response, exception ->
                Timber.tag(TAG)
                    .e("tokenExchangeRequest response:$response \n exception:$exception")
                if (exception != null) {
                    authState = AuthState()
                } else {
                    if (response != null) {
                        authState.update(response, exception)
                        response.idToken?.let { // Available only if user is authenticated, not needed for now
                            jwt = JWT(it)
                        }

                        oAuthToken = response.accessToken
                        Timber.tag(TAG)
                            .e("in handleAuthorizationResponse oAuthToken:$oAuthToken")
                        googleAuthTokenFlow.update { response.accessToken }
                    }
                }
            }
        }

        Timber.tag(TAG)
            .e("in handleAuthorizationResponse return from runBlocking with:$oAuthToken")
        return googleAuthTokenFlow.asStateFlow()
    }

    private fun initAuthServiceConfig() {
        authServiceConfig = AuthorizationServiceConfiguration(
            Uri.parse(AUTHORIZATION_ENDPOINT),
            Uri.parse(TOKEN_EXCHANGE_ENDPOINT),
            null,
            null
        )
    }

    private fun initAuthService() {
        // Allowing custom tabs to be opened inside the app, otherwise externally
        val appAuthConfiguration = AppAuthConfiguration.Builder()
            .setBrowserMatcher(
                BrowserAllowList(
                    VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                    VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
                )
            ).build()

        authorizationService = AuthorizationService(
            appContext,
            appAuthConfiguration
        )
    }

    companion object {
        const val TAG = "GOOGLE_AUTHORIZATION"

        const val CLIENT_ID = BuildConfig.GOOGLE_OAUTH_ANDROID_CLIENT_ID
        const val REDIRECT_URI = "io.photopixels.app:/oauth2redirect"

        // Define the authorization endpoint URL
        const val AUTHORIZATION_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth"
        const val TOKEN_EXCHANGE_ENDPOINT = "https://www.googleapis.com/oauth2/v4/token"

        const val MESSAGE_DIGEST_ALGORITHM = "SHA-256"
        const val CODE_VERIFIER_CHALLENGE_METHOD = "S256"

        // Scopes for Google Photos access (read-only)
        val SCOPES = listOf("https://www.googleapis.com/auth/photoslibrary.readonly")
    }
}
