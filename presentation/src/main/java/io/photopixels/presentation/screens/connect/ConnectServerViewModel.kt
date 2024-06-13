package io.photopixels.presentation.screens.connect

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.Response
import io.photopixels.domain.usecases.GetServerInfoUseCase
import io.photopixels.domain.usecases.GetServerStatusUseCase
import io.photopixels.domain.usecases.SetServerInfoUseCase
import io.photopixels.domain.usecases.ValidateFieldUseCase
import io.photopixels.domain.validation.ValidationRules
import io.photopixels.presentation.R
import io.photopixels.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectServerViewModel @Inject constructor(
    private val getServerStatusUseCase: GetServerStatusUseCase,
    private val setServerInfoUseCase: SetServerInfoUseCase,
    private val getServerInfoUseCase: GetServerInfoUseCase,
    private val validateFieldUseCase: ValidateFieldUseCase
) : BaseViewModel<ConnectServerState, ConnectServerActions, ConnectServerEvents>(ConnectServerState()) {

    init {
        getSavedServerAddress()
    }

    override suspend fun handleActions(action: ConnectServerActions) {
        when (action) {
            is ConnectServerActions.ConnectAction -> {
                if (!validateFieldUseCase.invoke(action.serverAddress, ValidationRules.SERVER_ADDRESS)) {
                    updateState {
                        copy(
                            serverAddress = serverAddress.copy(errorMsgId = R.string.connect_incorrect_address_msg)
                        )
                    }
                } else {
                    getServerStatus()
                }
            }

            ConnectServerActions.CloseErrorDialog -> {
                updateState { copy(errorMsgId = null) }
            }

            is ConnectServerActions.OnServerValueChanged -> {
                updateState { copy(serverAddress = serverAddress.copy(value = action.serverAddress)) }
            }
        }
    }

    private fun getServerStatus() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val serverAddressValue = state.value.serverAddress.value
            getServerStatusUseCase.invoke(serverAddressValue).collect { result ->
                if (result is Response.Success) {
                    updateState {
                        copy(
                            isLoading = false,
                            serverAddress = serverAddress.copy(value = serverAddressValue, errorMsgId = null)
                        )
                    }
                    submitEvent(ConnectServerEvents.NavigateToLoginScreen)
                    setServerInfoUseCase.setServerAddress(serverAddressValue)
                    setServerInfoUseCase.setServerVersion(result.result.serverVersion)
                } else if (result is Response.Failure) {
                    updateState {
                        copy(
                            isLoading = false,
                            errorMsgId = R.string.connect_error_msg,
                            serverAddress = serverAddress.copy(value = serverAddressValue, errorMsgId = null)
                        )
                    }
                }
            }
        }
    }

    private fun getSavedServerAddress() {
        viewModelScope.launch {
            val savedServerAddress = getServerInfoUseCase.getServerAddress()
            savedServerAddress?.let {
                updateState { copy(serverAddress = serverAddress.copy(value = it)) }
            }
        }
    }
}
