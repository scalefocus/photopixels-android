package com.scalefocus.presentation.screens.forgotpassword.mail

import com.scalefocus.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordMailViewModel @Inject constructor() :
    BaseViewModel<ForgotPasswordMailState, ForgotPasswordMailActions, Unit>(ForgotPasswordMailState()) {

        // TODO: Implement business logic

        override suspend fun handleActions(action: ForgotPasswordMailActions) {
            when (action) {
                is ForgotPasswordMailActions.OnSubmitClicked -> {
                }

                ForgotPasswordMailActions.CloseErrorDialog -> {
                    updateState { copy(errorMsgId = null) }
                }

                is ForgotPasswordMailActions.OnEmailValueChanged -> {
                    updateState { copy(email = email.copy(value = action.email)) }
                }
            }
        }
    }
