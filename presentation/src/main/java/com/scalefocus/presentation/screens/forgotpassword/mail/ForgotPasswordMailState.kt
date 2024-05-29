package com.scalefocus.presentation.screens.forgotpassword.mail

import androidx.annotation.StringRes
import com.scalefocus.presentation.base.textfield.SFTextEditField

data class ForgotPasswordMailState(
    val isLoading: Boolean = false,
    @StringRes val errorMsgId: Int? = null,
    val email: SFTextEditField = SFTextEditField(value = "")
)
