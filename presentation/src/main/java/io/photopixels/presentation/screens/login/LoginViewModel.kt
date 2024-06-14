package io.photopixels.presentation.screens.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.Response
import io.photopixels.domain.usecases.ClearUserDataUseCase
import io.photopixels.domain.usecases.GetLoggedUserUseCase
import io.photopixels.domain.usecases.auth.LoginUserUseCase
import io.photopixels.presentation.R
import io.photopixels.presentation.base.BaseViewModel
import io.photopixels.presentation.base.Constants
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserUseCase: GetLoggedUserUseCase,
    private val clearUserDataUseCase: ClearUserDataUseCase,
    savedState: SavedStateHandle
) : BaseViewModel<LoginScreenState, LoginScreenActions, LoginScreenEvents>(LoginScreenState()) {

    init {
        preFillUserCredentials(savedState)
    }

    override suspend fun handleActions(action: LoginScreenActions) {
        when (action) {
            is LoginScreenActions.LoginAction -> {
                loginUser(action.email, action.password)
            }

            LoginScreenActions.CloseErrorDialog -> updateState { copy(errorMsgId = null) }
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            loginUserUseCase.invoke(email, password).collect { response ->
                if (response is Response.Success) {
                    updateState { copy(isLoading = false) }
                    clearCurrentUserData(email)
                    submitEvent(LoginScreenEvents.NavigateToHomeScreen)
                } else if (response is Response.Failure) {
                    updateState { copy(isLoading = false, errorMsgId = R.string.login_error_msg) }
                }
            }
        }
    }

    /**
     * Clear user data if new user login in
     */
    private suspend fun clearCurrentUserData(newUserEmail: String) {
        val currentUserName = getUserUseCase.invoke()

        currentUserName?.let { oldUserEmail ->
            if (oldUserEmail != newUserEmail) {
                clearUserDataUseCase.invoke(clearServerData = false)
            }
        }
    }

    private fun preFillUserCredentials(savedState: SavedStateHandle) {
        viewModelScope.launch {
            // Pre-fill user credentials from registration screen
            savedState.get<String>(Constants.EMAIL_ARGUMENT_NAME)?.let { username ->
                savedState.get<String>(Constants.PASSWORD_ARGUMENT_NAME)?.let { password ->
                    updateState { copy(email = username, password = password) }
                }
            } ?: run {
                // Pre-fill previous logged username
                val loggedUserName = getUserUseCase.invoke()
                loggedUserName?.let {
                    updateState { copy(email = loggedUserName) }
                }
            }
        }
    }
}
