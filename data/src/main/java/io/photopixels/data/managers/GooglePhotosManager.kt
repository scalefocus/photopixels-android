package io.photopixels.data.managers

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.rpc.UnauthenticatedException
import com.google.auth.Credentials
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import com.google.photos.types.proto.MediaItem
import io.photopixels.data.BuildConfig
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.storage.database.GooglePhotosDao
import io.photopixels.data.storage.datastore.AuthDataStore
import io.photopixels.domain.base.PhotoPixelError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePhotosManager @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val googlePhotosDao: GooglePhotosDao
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var photosLibraryClient: PhotosLibraryClient? = null

    init {
        coroutineScope.launch {
            initPhotosLibrary()
        }
    }

    suspend fun fetchGooglePhotos(): PhotoPixelError? {
        if (photosLibraryClient == null) {
            Timber.tag(GOOGLE_PHOTOS_TAG).d("library is not initialized")
            initPhotosLibrary()
            Timber.tag(GOOGLE_PHOTOS_TAG).d("library initialized successfully")
        } else {
            Timber.tag(GOOGLE_PHOTOS_TAG).d("library initialized already")
        }

        return fetchPhotos()
    }

    private suspend fun initPhotosLibrary() {
        Timber.tag(GOOGLE_PHOTOS_TAG).d("init google photos library")
        val googleAuthToken = authDataStore.getGoogleAuthToken()
        googleAuthToken?.let {
            val settings = PhotosLibrarySettings
                .newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(getUserCredentials(it)))
                .build()

            photosLibraryClient = PhotosLibraryClient.initialize(settings)
            Timber.tag(GOOGLE_PHOTOS_TAG).d("Initialization successful")
        }
    }

    private fun getUserCredentials(googleAuthToken: String): Credentials {
        Timber.tag(GOOGLE_PHOTOS_TAG).d("init google photos library with google auth token:$googleAuthToken")
        val accessToken = AccessToken(googleAuthToken, null)
        return UserCredentials
            .newBuilder()
            .setClientId(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_ID)
            .setClientSecret(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_SECRET)
            .setAccessToken(accessToken)
            .build()
    }

    private suspend fun fetchPhotos(): PhotoPixelError? {
        val client = photosLibraryClient ?: return PhotoPixelError.GenericGoogleError.also {
            Timber.tag(GOOGLE_PHOTOS_TAG).e("PhotosLibrary not initialized")
        }

        return try {
            val mediaItems = client.listMediaItems().iterateAll().toList()
            // mediaItems.forEach { mediaItem ->
            // Access media item properties (id, baseUrl, etc.) as needed
            // val id = mediaItem.id
            // val baseUrl = mediaItem.baseUrl
            // Timber.tag(GOOGLE_PHOTOS_TAG).d("fetchPhotos: each item : $mediaItem")
            // }

            Timber.tag(GOOGLE_PHOTOS_TAG).d(
                "Google photos fetched successfully, " +
                    "saving ${mediaItems.size} photos metadata to DB"
            )
            savePhotosDataToDB(mediaItems)
            null // Success - no error
        } catch (e: UnauthenticatedException) {
            Timber.tag(GOOGLE_PHOTOS_TAG).e("fetchPhotos: UnauthenticatedException(Token not valid):$e")
            photosLibraryClient = null
            PhotoPixelError.ExpiredGoogleAuthTokenError
        } catch (e: Exception) {
            Timber.tag(GOOGLE_PHOTOS_TAG).e("fetchPhotos: Error while fetching photos:$e")
            // TODO: Log this error in Analytics server
            PhotoPixelError.GenericGoogleError
        }
    }

    private suspend fun savePhotosDataToDB(mediaItemsList: List<MediaItem>) {
        val googlePhotosList = mediaItemsList.map { it.toEntity() }
        googlePhotosDao.insertPhotosData(googlePhotosList)
    }

    companion object {
        private const val GOOGLE_PHOTOS_TAG = "GOOGLE_PHOTOS"
    }
}
