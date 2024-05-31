package com.scalefocus.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(val email: String, val newPassword: String, val verificationCode: String)
