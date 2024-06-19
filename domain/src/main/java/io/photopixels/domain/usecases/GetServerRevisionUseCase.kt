package io.photopixels.domain.usecases

import io.photopixels.domain.base.Response
import io.photopixels.domain.model.ServerRevision
import io.photopixels.domain.repository.ServerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServerRevisionUseCase @Inject constructor(private val serverRepository: ServerRepository) {
    suspend operator fun invoke(specificRevision: Int?): Flow<Response<ServerRevision>> {
        return serverRepository.getServerRevision(specificRevision)
    }
}
