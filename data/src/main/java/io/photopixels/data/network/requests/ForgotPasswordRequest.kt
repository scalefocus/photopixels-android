package io.photopixels.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(val email: String)
