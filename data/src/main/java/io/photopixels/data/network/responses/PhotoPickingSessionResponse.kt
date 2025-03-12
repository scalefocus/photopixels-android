package io.photopixels.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class PhotoPickingSessionResponse(
    val expireTime: String,
    val id: String,
    val mediaItemsSet: Boolean,
    val pickerUri: String,
    val pickingConfig: PickingConfig?,
    val pollingConfig: PollingConfig?
) {

    @Serializable
    data class PickingConfig(
        val maxItemCount: String
    )

    @Serializable
    data class PollingConfig(
        val pollInterval: String,
        val timeoutIn: String
    )
}
