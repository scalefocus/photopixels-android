package com.scalefocus.domain.usecases

import com.scalefocus.domain.base.Response
import com.scalefocus.domain.model.ServerStatus
import com.scalefocus.domain.repository.ServerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServerStatusUseCase @Inject constructor(private val serverRepository: ServerRepository) {
    suspend fun invoke(serverAddress: String): Flow<Response<ServerStatus>> {
        return serverRepository.getServerStatus(serverAddress)
    }
}
