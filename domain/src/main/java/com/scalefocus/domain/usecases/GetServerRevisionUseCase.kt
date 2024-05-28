package com.scalefocus.domain.usecases

import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.ServerRevision
import com.scalefocus.domain.repository.ServerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServerRevisionUseCase @Inject constructor(private val serverRepository: ServerRepository) {
    suspend operator fun invoke(specificRevision: Int?): Flow<Response<ServerRevision>> {
        return serverRepository.getServerRevision(specificRevision)
    }
}
