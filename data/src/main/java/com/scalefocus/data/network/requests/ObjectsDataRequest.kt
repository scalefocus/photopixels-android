package com.scalefocus.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class ObjectsDataRequest(val objectIds: List<String>)
