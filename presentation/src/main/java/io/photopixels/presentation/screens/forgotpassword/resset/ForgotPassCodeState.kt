package io.photopixels.presentation.screens.forgotpassword.resset

import androidx.annotation.StringRes
import io.photopixels.presentation.base.textfield.SFTextEditField

data class ForgotPassCodeState(
    val password: SFTextEditField = SFTextEditField(value = ""),
    val confirmPassword: SFTextEditField = SFTextEditField(value = ""),
    val isLoading: Boolean = false,
    @StringRes val errorMsgId: Int? = null,
    @StringRes val successMsgId: Int? = null,
)
