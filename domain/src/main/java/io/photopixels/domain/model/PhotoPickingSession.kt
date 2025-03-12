package io.photopixels.domain.model

data class PhotoPickingSession(
    val expireTime: String,
    val id: String,
    val mediaItemsSet: Boolean,
    val pickerUri: String,
    val pickingConfig: PickingConfig?,
    val pollingConfig: PollingConfig?
) {

    data class PickingConfig(
        val maxItemCount: String
    )

    data class PollingConfig(
        val pollInterval: String,
        val timeoutIn: String
    )
}
