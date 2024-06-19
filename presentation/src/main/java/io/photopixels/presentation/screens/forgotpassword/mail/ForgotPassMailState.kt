package io.photopixels.presentation.screens.forgotpassword.mail

import androidx.annotation.StringRes
import io.photopixels.presentation.base.textfield.SFTextEditField

data class ForgotPassMailState(
    val isLoading: Boolean = false,
    @StringRes val errorMsgId: Int? = null,
    @StringRes val successMsgId: Int? = null,
    val email: SFTextEditField = SFTextEditField(value = "")
)
