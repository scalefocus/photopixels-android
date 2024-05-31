package com.scalefocus.presentation.screens.forgotpassword.resset

import com.scalefocus.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordCodeViewModel @Inject constructor() :
    BaseViewModel<ForgotPasswordCodeState, ForgotPasswordCodeActions, Unit>(ForgotPasswordCodeState()) {

        override suspend fun handleActions(action: ForgotPasswordCodeActions) {
            when (action) {
                ForgotPasswordCodeActions.OnSubmitClicked -> {
                    // TODO: To be impl
                }

                ForgotPasswordCodeActions.CloseErrorDialog -> {
                    // TODO: To be impl
                }

                is ForgotPasswordCodeActions.OnConfirmPasswordChange -> {
                    // TODO: To be impl
                }

                is ForgotPasswordCodeActions.OnPasswordChange -> {
                    // TODO: To be impl
                }
            }
        }
    }
