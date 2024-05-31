package com.scalefocus.presentation.screens.forgotpassword.resset

import androidx.annotation.StringRes
import com.scalefocus.presentation.base.textfield.SFTextEditField

data class ForgotPasswordCodeState(
    val password: SFTextEditField = SFTextEditField(value = ""),
    val confirmPassword: SFTextEditField = SFTextEditField(value = ""),
    val isLoading: Boolean = false,
    @StringRes val errorMsgId: Int? = null
)
