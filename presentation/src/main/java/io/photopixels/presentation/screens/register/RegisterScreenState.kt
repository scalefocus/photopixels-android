package io.photopixels.presentation.screens.register

import io.photopixels.presentation.base.textfield.SFTextEditField

data class RegisterScreenState(
    val name: SFTextEditField = SFTextEditField(value = ""),
    val email: SFTextEditField = SFTextEditField(value = ""),
    val password: SFTextEditField = SFTextEditField(value = ""),
    val confirmPassword: SFTextEditField = SFTextEditField(value = ""),
    val isLoading: Boolean = false,
    val errorMsgId: Int? = null
)
