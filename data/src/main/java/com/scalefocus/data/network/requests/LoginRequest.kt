package com.scalefocus.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)
