package com.scalefocus.presentation.screens.register

import androidx.lifecycle.viewModelScope
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.usecases.RegisterUserUseCase
import com.scalefocus.presentation.R
import com.scalefocus.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : BaseViewModel<RegisterScreenState, RegisterScreenActions, RegisterScreenEvents>(RegisterScreenState()) {

    override suspend fun handleActions(action: RegisterScreenActions) {
        when (action) {
            is RegisterScreenActions.RegisterAction -> {
                registerUser(action.name, action.email, action.password)
            }

            RegisterScreenActions.CloseErrorDialog -> updateState { copy(errorMsgId = null) }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            registerUserUseCase.invoke(name, email, password).collect { response ->
                if (response is Response.Success) {
                    updateState { copy(isLoading = false) }
                    submitEvent(RegisterScreenEvents.NavigateToLoginScreen(email, password))
                } else if (response is Response.Failure) {
                    updateState {
                        copy(
                            isLoading = false,
                            errorMsgId = R.string.register_error_msg
                        )
                    }
                }
            }
        }
    }
}
