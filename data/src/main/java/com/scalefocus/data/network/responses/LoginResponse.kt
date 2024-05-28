package com.scalefocus.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val tokenType: String,
    val accessToken: String,
    val expiresIn: Int,
    val refreshToken: String,
    val userId: String
)
