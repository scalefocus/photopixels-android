package io.photopixels.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.model.UserSettings
import io.photopixels.domain.usecases.ClearUserDataUseCase
import io.photopixels.domain.usecases.GetAppInfoData
import io.photopixels.domain.usecases.GetUserSettingsUseCase
import io.photopixels.domain.usecases.SaveGoogleAuthTokenUseCase
import io.photopixels.domain.usecases.SetUserSettingsUseCase
import io.photopixels.domain.usecases.googlephotos.GetGooglePhotosUseCase
import io.photopixels.domain.workers.WorkerStarter
import io.photopixels.presentation.R
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.login.GoogleAuthorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val getAppInfoDataUseCase: GetAppInfoData,
    private val clearUserDataUseCase: ClearUserDataUseCase,
    private val saveGoogleAuthTokenUseCase: SaveGoogleAuthTokenUseCase,
    private val getGooglePhotosUseCase: GetGooglePhotosUseCase,
    private val googleAuthorization: GoogleAuthorization,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val setUserSettingsUseCase: SetUserSettingsUseCase,
    private val workerStarter: WorkerStarter
) : BaseViewModel<SettingsScreenState, SettingsScreenActions, SettingsScreenEvents>(SettingsScreenState()) {

    private var userSettings: UserSettings = UserSettings()

    init {
        loadUserSettings()
    }

    override suspend fun handleActions(action: SettingsScreenActions) {
        when (action) {
            is SettingsScreenActions.LoadSettingsData -> {
                loadSettingsData(action.appVersion)
            }

            SettingsScreenActions.OnLogoutClicked -> {
                logOutUser()
            }

            is SettingsScreenActions.OnSyncGooglePhotosClicked -> {
                if (action.isChecked) {
                    authorizeWithGoogle()
                } else {
                    stopGoogleSync()
                }
            }

            SettingsScreenActions.CloseErrorDialog -> {
                updateState { copy(messageId = null) }
            }

            is SettingsScreenActions.OnGoogleOauthIntentReceived -> {
                viewModelScope.launch {
                    googleAuthorization.handleAuthorizationResponse(action.intent).collect { googleAuthToken ->
                        googleAuthToken?.let {
                            onGoogleLoginSuccess(it)
                        }
                    }
                }
            }

            is SettingsScreenActions.OnRequirePowerClicked -> {
                updateState { copy(userSettings = userSettings.copy(requirePower = action.isChecked)) }
                userSettings = userSettings.copy(requirePower = action.isChecked)
                setUserSettingsUseCase.invoke(userSettings)
            }

            is SettingsScreenActions.OnRequireWifiClicked -> {
                updateState { copy(userSettings = userSettings.copy(requireWifi = action.isChecked)) }
                userSettings = userSettings.copy(requireWifi = action.isChecked)
                setUserSettingsUseCase.invoke(userSettings)
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
                    userSettings = userSettings.copy(syncWithGoogle = true)
                )
            }

            userSettings = userSettings.copy(syncWithGoogle = true)
            setUserSettingsUseCase.invoke(userSettings)

            async(Dispatchers.IO) { getGooglePhotosUseCase.invoke() }.await()
            workerStarter.startGooglePhotosWorker()
        }
    }

    private fun stopGoogleSync() {
        viewModelScope.launch {
            updateState {
                copy(
                    userSettings = userSettings.copy(syncWithGoogle = false)
                )
            }
            userSettings = userSettings.copy(syncWithGoogle = false)
            setUserSettingsUseCase.invoke(userSettings)
            workerStarter.stopGooglePhotosWorker()
        }
    }

    private fun loadUserSettings() {
        viewModelScope.launch {
            val settings = getUserSettingsUseCase.invoke()
            settings?.let {
                userSettings = it

                updateState {
                    copy(userSettings = it)
                }
            }
        }
    }
}
