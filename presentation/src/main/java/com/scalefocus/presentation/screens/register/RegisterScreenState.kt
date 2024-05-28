package com.scalefocus.presentation.screens.register

import com.scalefocus.presentation.base.textfield.SFTextEditField

data class RegisterScreenState(
    val name: SFTextEditField = SFTextEditField(value = ""),
    val email: SFTextEditField = SFTextEditField(value = ""),
    val password: SFTextEditField = SFTextEditField(value = ""),
    val confirmPassword: SFTextEditField = SFTextEditField(value = ""),
    val isLoading: Boolean = false,
    val errorMsgId: Int? = null
)
