package io.photopixels.presentation.screens.connect

import androidx.annotation.StringRes
import io.photopixels.presentation.base.textfield.SFTextEditField

data class ConnectServerState(
    val serverAddress: SFTextEditField = SFTextEditField(value = ""),
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    @StringRes val errorMsgId: Int? = null
)
