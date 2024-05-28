package com.scalefocus.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import com.scalefocus.domain.usecases.ClearUserDataUseCase
import com.scalefocus.domain.usecases.GetAppInfoData
import com.scalefocus.domain.usecases.SaveGoogleAuthTokenUseCase
import com.scalefocus.domain.usecases.googlephotos.GetGooglePhotosUseCase
import com.scalefocus.presentation.R
import com.scalefocus.presentation.base.BaseViewModel
import com.scalefocus.presentation.login.GoogleAuthorization
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val getAppInfoDataUseCase: GetAppInfoData,
    private val clearUserDataUseCase: ClearUserDataUseCase,
    private val saveGoogleAuthTokenUseCase: SaveGoogleAuthTokenUseCase,
    private val getGooglePhotosUseCase: GetGooglePhotosUseCase,
    private val googleAuthorization: GoogleAuthorization
) : BaseViewModel<SettingsScreenState, SettingsScreenActions, SettingsScreenEvents>(SettingsScreenState()) {

    // TODO Implement logic for load/save user-prefs
    override suspend fun handleActions(action: SettingsScreenActions) {
        when (action) {
            is SettingsScreenActions.LoadSettingsData -> {
                loadSettingsData(action.appVersion)
            }

            SettingsScreenActions.OnLogoutClicked -> {
                logOutUser()
            }

            is SettingsScreenActions.OnSyncGooglePhotosClicked -> {
                Timber.tag("TAG").e("OnSyncGooglePhotosClicked checked:${action.isChecked}")
                if (action.isChecked) {
                    // loginWithGoogle(action.activityContext) Sign-in with Google is not needed for now

                    authorizeWithGoogle()
                } else {
                    // TODO Stop Google-Photos syncing if active
                    // TODO TBD should we also logout user from google if uncheck this option?
                }
            }

            SettingsScreenActions.CloseErrorDialog -> {
                updateState { copy(messageId = null) }
            }

            is SettingsScreenActions.OnGoogleOauthIntentReceived -> {
                viewModelScope.launch {
                    Timber.tag("TAG").e("SettingsScreenViewMo OnGoogleOauthIntentReceived action received!!!!")
                    googleAuthorization.handleAuthorizationResponse(action.intent).collect { googleAuthToken ->
                        googleAuthToken?.let {
                            onGoogleLoginSuccess(it)
                        }
                    }
                }
            }
        }
    }

    private fun logOutUser() {
        viewModelScope.launch {
            clearUserDataUseCase.invoke()
            submitEvent(SettingsScreenEvents.NavigateToConnectServerScreen)
        }
    }

    private fun loadSettingsData(appVersion: String) {
        viewModelScope.launch {
            val appInfoData = getAppInfoDataUseCase.invoke(appVersion)
            updateState { copy(appInfoData = appInfoData) }
        }
    }

    private fun authorizeWithGoogle() {
        val intent = googleAuthorization.generateAuthorizationIntent()
        Timber.tag("TAG").e("Received intent in VM!!!")
        intent?.let {
            submitEvent(SettingsScreenEvents.StartAuthorizationIntent(it))
        }
    }

    private fun onGoogleLoginSuccess(googleAuthToken: String) {
        viewModelScope.launch {
            saveGoogleAuthTokenUseCase.invoke(googleAuthToken)
            updateState {
                copy(
                    messageId = R.string.settings_screen_google_login_success,
                    isGoogleSyncEnabled = true
                )
            }
            // TODO trigger Google photos sync worker
            Timber.tag("TAG").e("SettingsScreenViewModel() invoking getGooglePhotosUseCase.invoke()")
            getGooglePhotosUseCase.invoke()
        }
    }
}
