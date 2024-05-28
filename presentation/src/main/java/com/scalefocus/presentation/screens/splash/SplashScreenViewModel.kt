package com.scalefocus.presentation.screens.splash

import androidx.lifecycle.viewModelScope
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.usecases.GetServerInfoUseCase
import com.scalefocus.domain.usecases.GetServerRevisionUseCase
import com.scalefocus.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MaxLineLength", "ktlint:standard:max-line-length")
@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getServerRevisionUseCase: GetServerRevisionUseCase,
    private val getServerInfoUseCase: GetServerInfoUseCase
) : BaseViewModel<SplashScreenState, Unit, SplashScreenEvents>(SplashScreenState()) {

    init {
        viewModelScope.launch {
            tryAutoLogin()
        }
    }

    // Try to get server revision with saved credentials, if successful go to Login screen,
    // otherwise proceed with normal connect flow
    private fun tryAutoLogin() {
        getServerRevision()
    }

    private fun getServerRevision() {
        viewModelScope.launch {
            val serverInfo = getServerInfoUseCase.getServerAddress()
            serverInfo?.let {
                getServerRevisionUseCase.invoke(specificRevision = 0).collect {
                    if (it is Response.Success) {
                        submitEvent(SplashScreenEvents.NavigateToHomeScreen)
                    } else if (it is Response.Failure) {
                        submitEvent(SplashScreenEvents.NavigateToConnectServerScreen)
                    }
                }
            } ?: run {
                submitEvent(SplashScreenEvents.NavigateToConnectServerScreen)
            }
        }
    }
}
