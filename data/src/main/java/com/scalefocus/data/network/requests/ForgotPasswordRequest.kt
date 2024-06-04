package com.scalefocus.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(val email: String)
