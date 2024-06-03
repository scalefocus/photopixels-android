package com.scalefocus.data.network.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    @SerialName("password")
    val newPassword: String,
    @SerialName("code")
    val verificationCode: String
)
