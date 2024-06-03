package com.scalefocus.presentation.screens.forgotpassword.mail

import androidx.lifecycle.viewModelScope
import com.scalefocus.domain.base.Response
import com.scalefocus.domain.usecases.ValidateFieldUseCase
import com.scalefocus.domain.usecases.auth.ForgotPasswordUseCase
import com.scalefocus.domain.validation.ValidationRules
import com.scalefocus.presentation.R
import com.scalefocus.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPassMailViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val validateFieldUseCase: ValidateFieldUseCase
) :
    BaseViewModel<ForgotPassMailState, ForgotPassMailActions, ForgotPassMailEvents>(
            ForgotPassMailState()
        ) {

        override suspend fun handleActions(action: ForgotPassMailActions) {
            when (action) {
                is ForgotPassMailActions.OnSubmitClicked -> {
                    if (validateEmail(state.value.email.value)) {
                        requestPasswordReset(state.value.email.value)
                    }
                }

                ForgotPassMailActions.CloseErrorDialog -> {
                    updateState { copy(errorMsgId = null) }
                }

                is ForgotPassMailActions.OnEmailValueChanged -> {
                    updateState { copy(email = email.copy(value = action.email)) }
                }
            }
        }

        private suspend fun validateEmail(emailValue: String): Boolean {
            val isEmailValid = validateFieldUseCase.invoke(emailValue, ValidationRules.EMAIL)

            val errorMsgId = if (!isEmailValid) R.string.register_email_incorrect else null
            updateState { copy(email = email.copy(errorMsgId = errorMsgId)) }

            return isEmailValid
        }

        private suspend fun requestPasswordReset(email: String) {
            viewModelScope.launch {
                updateState { copy(isLoading = true) }
                val result = forgotPasswordUseCase.forgotPassword(email)
                if (result is Response.Success) {
                    updateState { copy(isLoading = false, successMsgId = R.string.forgot_pass_mail_sent_msg) }
                    submitEvent(ForgotPassMailEvents.NavigateToResetPassScreen(email))
                } else if (result is Response.Failure) {
                    // NOTE: When email is not found, WEB app is showing generic error, probably for security reasons
                    updateState { copy(isLoading = false, errorMsgId = R.string.error_generic) }
                }
            }
        }
    }
