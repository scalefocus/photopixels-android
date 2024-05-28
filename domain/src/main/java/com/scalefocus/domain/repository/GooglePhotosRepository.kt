package com.scalefocus.domain.repository

interface GooglePhotosRepository {
    suspend fun fetchGooglePhotos()
}
