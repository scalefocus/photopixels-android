package com.scalefocus.presentation.screens.login

data class LoginScreenState(
    val isLoading: Boolean = false,
    val errorMsgId: Int? = null,
    val email: String = "",
    val password: String = ""
)
