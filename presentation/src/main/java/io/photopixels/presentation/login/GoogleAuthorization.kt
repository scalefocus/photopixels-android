package io.photopixels.presentation.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.auth0.android.jwt.JWT
import dagger.hilt.android.qualifiers.ApplicationContext
import io.photopixels.domain.usecases.auth.GetGoogleAuthStateUseCase
import io.photopixels.domain.usecases.auth.SaveGoogleAuthStateUseCase
import io.photopixels.presentation.BuildConfig
import io.photopixels.presentation.utils.GoogleAuthorizationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
import org.json.JSONException
import timber.log.Timber
import javax.inject.Inject

/**
 * This class is used to do user Authorization via appAuth library
 * Note: This class should be used if application needs user's permissions for accessing his data related to some Google APIs(drive, photos and etc..)
 */
class GoogleAuthorization @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val saveGoogleAuthStateUseCase: SaveGoogleAuthStateUseCase,
    private val getGoogleAuthStateUseCase: GetGoogleAuthStateUseCase
) {
    private lateinit var authorizationService: AuthorizationService
    private var authState: AuthState = AuthState()
    private var jwt: JWT? = null
    private lateinit var authServiceConfig: AuthorizationServiceConfiguration
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Google auth token for accessing Google APIs
    private val googleAuthTokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val refreshTokenFlow: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    init {
        // AppAuth library initialization
        loadAuthState()
        initAuthServiceConfig()
        initAuthService()
    }

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

        val builder = AuthorizationRequest
            .Builder(
                authServiceConfig,
                CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse(REDIRECT_URI)
            ).setCodeVerifier(
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
                Timber
                    .tag(TAG)
                    .d("tokenExchangeRequest response:$response \n exception:$exception")
                if (exception != null) {
                    authState = AuthState()
                } else {
                    if (response != null) {
                        authState.update(response, exception)
                        response.idToken?.let {
                            // Available only if user is authenticated, not needed for now
                            jwt = JWT(it)
                        }

                        oAuthToken = response.accessToken
                        Timber
                            .tag(TAG)
                            .d("in handleAuthorizationResponse oAuthToken:$oAuthToken")
                        googleAuthTokenFlow.update { response.accessToken }
                        saveAuthState()
                    }
                }
            }
        }

        return googleAuthTokenFlow.asStateFlow()
    }

    fun loadAuthState() {
        coroutineScope.launch {
            val authStateJson = getGoogleAuthStateUseCase.invoke()

            authStateJson?.let {
                try {
                    authState = AuthState.jsonDeserialize(it)
                    Timber.tag(TAG).d("Google Auth State LOADED Successfully:$it")
                } catch (ex: JSONException) {
                    Timber.tag(TAG).e("Error loading Google Auth State: ${ex.message}")
                }
            } ?: run {
                Timber.tag(TAG).d("Google Auth State -> Nothing to load...")
            }
        }
    }

    fun performRefreshTokenRequest(): StateFlow<Boolean?> {
        Timber.tag(TAG).d("Perform Google refresh token request")

        authorizationService.performTokenRequest(
            authState.createTokenRefreshRequest()
        ) { response, exception ->

            if (exception != null) {
                authState = AuthState()
                Timber.tag(TAG).e("Google refresh token request failed:${exception.message}")
                refreshTokenFlow.update { false }
            } else {
                if (response != null) {
                    Timber.tag(TAG).d("Google refresh token request completed successfully")
                    authState.update(response, exception)

                    saveAuthState()
                    refreshTokenFlow.update { true }
                    googleAuthTokenFlow.update { response.accessToken }
                }
            }
        }

        return refreshTokenFlow.asStateFlow()
    }

    fun getGoogleAuthTokenFlow(): StateFlow<String?> = googleAuthTokenFlow.asStateFlow()

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
        val appAuthConfiguration = AppAuthConfiguration
            .Builder()
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

    private fun saveAuthState() {
        coroutineScope.launch {
            Timber.tag(TAG).d("Saving google Auth State to DataStore")
            val authStateJson = authState.jsonSerializeString()
            saveGoogleAuthStateUseCase.invoke(authStateJson)
        }
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
