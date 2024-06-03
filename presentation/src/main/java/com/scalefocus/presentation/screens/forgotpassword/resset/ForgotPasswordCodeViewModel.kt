package com.scalefocus.presentation.screens.forgotpassword.resset

import androidx.lifecycle.SavedStateHandle
import com.scalefocus.domain.base.PhotoPixelError
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.usecases.ValidateFieldUseCase
import com.scalefocus.domain.usecases.auth.ForgotPasswordUseCase
import com.scalefocus.domain.validation.ValidationRules
import com.scalefocus.presentation.R
import com.scalefocus.presentation.base.BaseViewModel
import com.scalefocus.presentation.base.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordCodeViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val validateFieldUseCase: ValidateFieldUseCase,
    savedState: SavedStateHandle
) :
    BaseViewModel<ForgotPasswordCodeState, ForgotPasswordCodeActions, ForgotPasswordCodeEvents>(
            ForgotPasswordCodeState()
        ) {
        private lateinit var email: String

        init {
            savedState.get<String>(Constants.EMAIL_ARGUMENT_NAME)?.let {
                email = it
            }
        }

        override suspend fun handleActions(action: ForgotPasswordCodeActions) {
            when (action) {
                is ForgotPasswordCodeActions.OnSubmitClicked -> {
                    if (validatePassword(state.value.password.value, state.value.confirmPassword.value)) {
                        resetPassword(email = email, verificationCode = action.verificationCode)
                    }
                }

                ForgotPasswordCodeActions.CloseErrorDialog -> {
                    updateState { copy(errorMsgId = null) }
                }

                is ForgotPasswordCodeActions.OnPasswordChange -> {
                    updateState { copy(password = password.copy(value = action.password)) }
                }

                is ForgotPasswordCodeActions.OnConfirmPasswordChange -> {
                    updateState { copy(confirmPassword = confirmPassword.copy(value = action.confirmPassword)) }
                }
            }
        }

        private suspend fun resetPassword(email: String, verificationCode: String) {
            updateState { copy(isLoading = true) }

            val result = forgotPasswordUseCase.resetPassword(email, verificationCode, state.value.password.value)

            if (result is Response.Success) {
                updateState { copy(isLoading = false, successMsgId = R.string.forgot_pass_success_msg) }
                submitEvent(ForgotPasswordCodeEvents.NavigateToLoginScreen)
            } else if (result is Response.Failure) {
                if (result.error is PhotoPixelError.VerificationCodeIncorrect) {
                    updateState { copy(isLoading = false, errorMsgId = R.string.forgot_pass_error_incorrect_code) }
                } else {
                    updateState { copy(isLoading = false, errorMsgId = R.string.error_generic) }
                }

                submitEvent(ForgotPasswordCodeEvents.NavigateToLoginScreen)
            }
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
    }
