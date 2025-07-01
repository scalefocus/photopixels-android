package io.photopixels.data.repository

import android.net.Uri
import io.photopixels.data.mappers.toDomain
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.mappers.toThumbnail
import io.photopixels.data.network.BackendApi
import io.photopixels.data.network.tus.ResumableUploadApi
import io.photopixels.data.storage.database.ThumbnailsDao
import io.photopixels.data.storage.database.entities.ThumbnailsEntity
import io.photopixels.domain.base.Response
import io.photopixels.domain.model.PhotoUploadData
import io.photopixels.domain.model.ServerMedia
import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.repository.ServerMediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

internal class ServerMediaRepositoryImpl @Inject constructor(
    private val thumbnailsDao: ThumbnailsDao,
    private val backendApi: BackendApi,
    private val resumableUploadApi: ResumableUploadApi,
) : ServerMediaRepository {

    override suspend fun getMediaByHash(hash: String): ServerMedia? = thumbnailsDao.getThumbnailByHash(hash)?.toDomain()

    override suspend fun getServerThumbnails(serverItemHashIds: List<String>): Response<List<ServerMedia>> = backendApi
        .getThumbnailsByIds(
            serverItemHashIds
        )

    override suspend fun uploadMedia(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        androidCloudId: String,
        objectHash: String
    ): Response<PhotoUploadData> = backendApi.uploadPhoto(fileBytes, fileName, mimeType, androidCloudId, objectHash)

    override suspend fun uploadMedia(
        uri: Uri,
        fileName: String,
        objectHash: String,
    ) = resumableUploadApi.uploadFile(uri, fileName, objectHash)

    override suspend fun insertThumbnailsToDb(thumbnailsList: List<ServerMedia>) {
        thumbnailsDao.insertThumbnailPhotos(thumbnailsList.map { it.toEntity() })
    }

    override fun getRemoteThumbnailsFromDb(): Flow<List<Thumbnail.RemoteThumbnail>> =
        thumbnailsDao.getAllThumbnails().mapLatest {
            it.map(ThumbnailsEntity::toThumbnail)
        }

    override suspend fun clearNewlyUploadedThumbnails() {
        thumbnailsDao.getAllNewlyUploadedThumbnails()
            .map { it.copy(isNewlyUploaded = false) }
            .takeIf { it.isNotEmpty() }
            ?.let { thumbnailsDao.updateThumbnailPhotos(it) }
    }

    override suspend fun getThumbnailsFromDbCount(): Int = thumbnailsDao.getThumbnailsCount()

    override suspend fun deleteThumbnailsFromDb(ids: List<String>) {
        thumbnailsDao.deletePhotos(ids)
    }

    override suspend fun clearThumbnailsTable() {
        thumbnailsDao.clearTable()
    }

    override suspend fun deleteMedia(mediaServerId: String): Response<Unit> = backendApi.deletePhoto(mediaServerId)
}
