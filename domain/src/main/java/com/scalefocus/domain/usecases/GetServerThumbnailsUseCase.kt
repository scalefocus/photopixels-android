package com.scalefocus.domain.usecases

import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.PhotoUiData
import com.scalefocus.domain.repository.PhotosRepository
import javax.inject.Inject

class GetServerThumbnailsUseCase @Inject constructor(private val photosRepository: PhotosRepository) {
    suspend operator fun invoke(serverItemIds: List<String>): Response<List<PhotoUiData>> {
        return photosRepository.getServerThumbnails(serverItemIds)
    }
}
