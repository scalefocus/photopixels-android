package com.scalefocus.data.storage.memory

import javax.inject.Inject

class MemoryStorage @Inject constructor() {
    // Storing all user photos serverIds in memory -> This can be improved by adding pagination in BE
    private val _photosIdsList = mutableListOf<String>()

    val photosIdsList: List<String>
        get() {
            return _photosIdsList.toList()
        }

    fun addPhotosServerIdsList(photosIdsList: List<String>) {
        _photosIdsList.clear()
        _photosIdsList.addAll(photosIdsList)
    }
}
