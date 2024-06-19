package io.photopixels.presentation.base.textfield

import androidx.annotation.StringRes

data class SFTextEditField(
    var value: String,
    @StringRes var errorMsgId: Int? = null
)
