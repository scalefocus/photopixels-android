package io.photopixels.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ObjectUploadResponse(val id: String, val revision: Int, val quota: Long, val usedQuota: Long)
