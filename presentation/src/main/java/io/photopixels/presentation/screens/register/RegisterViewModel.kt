package io.photopixels.presentation.screens.register

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.photopixels.domain.base.PhotoPixelError
import io.photopixels.domain.base.Response
import io.photopixels.domain.usecases.ValidateFieldUseCase
import io.photopixels.domain.usecases.auth.RegisterUserUseCase
import io.photopixels.domain.validation.ValidationRules
import io.photopixels.presentation.R
import io.photopixels.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val validateFieldUseCase: ValidateFieldUseCase
) : BaseViewModel<RegisterScreenState, RegisterScreenActions, RegisterScreenEvents>(RegisterScreenState()) {

    override suspend fun handleActions(action: RegisterScreenActions) {
        when (action) {
            is RegisterScreenActions.RegisterAction -> {
                val name = state.value.name.value
                val email = state.value.email.value
                val password = state.value.password.value
                val confirmPassword = state.value.confirmPassword.value

                if (validateFields(name, email, password, confirmPassword)) {
                    registerUser(name, email, password)
                }
            }

            RegisterScreenActions.CloseErrorDialog -> updateState { copy(errorMsgId = null) }

            is RegisterScreenActions.OnNameValueChanged -> updateState { copy(name = name.copy(value = action.name)) }
            is RegisterScreenActions.OnEmailValueChanged -> updateState {
                copy(
                    email = email.copy(value = action.email)
                )
            }

            is RegisterScreenActions.OnPasswordValueChanged -> updateState {
                copy(
                    password = password.copy(value = action.password)
                )
            }

            is RegisterScreenActions.OnConfirmPasswordValueChanged -> updateState {
                copy(
                    confirmPassword = confirmPassword.copy(
                        value = action.confirmPassword
                    )
                )
            }
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
                    val errorMsgId: Int = if (response.error is PhotoPixelError.AccountAlreadyTaken) {
                        R.string.register_account_taken_error
                    } else {
                        R.string.register_error_msg
                    }

                    updateState {
                        copy(
                            isLoading = false,
                            errorMsgId = errorMsgId
                        )
                    }
                }
            }
        }
    }

    private suspend fun validateFields(name: String, email: String, pass: String, confirmPass: String): Boolean {
        val isNameValid = validateName(name)
        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(pass, confirmPass)

        return isPasswordValid && isNameValid && isEmailValid
    }

    private suspend fun validatePassword(pass: String, confirmPass: String): Boolean {
        var isPasswordValid = true

        // Check passwords match
        var errorMsgId: Int? = if (pass != confirmPass) R.string.register_error_pass_not_match else null
        updateState { copy(password = password.copy(errorMsgId = errorMsgId)) }
        if (errorMsgId != null) return false

        // Check password strength
        isPasswordValid = validateFieldUseCase.invoke(pass, ValidationRules.PASSWORD)
        errorMsgId = if (!isPasswordValid) R.string.register_pass_not_strong else null
        updateState { copy(password = password.copy(errorMsgId = errorMsgId)) }

        return isPasswordValid
    }

    private suspend fun validateName(nameValue: String): Boolean {
        val isNameValid = validateFieldUseCase.invoke(nameValue, ValidationRules.NAME)

        val errorMsgId = if (!isNameValid) R.string.register_name_incorrect else null
        updateState { copy(name = name.copy(errorMsgId = errorMsgId)) }

        return isNameValid
    }

    private suspend fun validateEmail(emailValue: String): Boolean {
        val isEmailValid = validateFieldUseCase.invoke(emailValue, ValidationRules.EMAIL)

        val errorMsgId = if (!isEmailValid) R.string.register_email_incorrect else null
        updateState { copy(email = email.copy(errorMsgId = errorMsgId)) }

        return isEmailValid
    }
}
