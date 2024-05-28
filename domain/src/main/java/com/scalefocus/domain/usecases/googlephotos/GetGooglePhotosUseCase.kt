package com.scalefocus.domain.usecases.googlephotos

import com.scalefocus.domain.repository.GooglePhotosRepository
import javax.inject.Inject

class GetGooglePhotosUseCase @Inject constructor(private val googlePhotosRepository: GooglePhotosRepository) {
    suspend fun invoke() {
        googlePhotosRepository.fetchGooglePhotos()
    }
}
