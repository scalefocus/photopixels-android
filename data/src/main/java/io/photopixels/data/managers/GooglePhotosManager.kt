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
    private lateinit var photosLibraryClient: PhotosLibraryClient

    init {
        coroutineScope.launch {
            initPhotosLibrary()
        }
    }

    suspend fun fetchGooglePhotos(): PhotoPixelError? = fetchPhotos()

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

    @Suppress("ReturnCount")
    private suspend fun fetchPhotos(): PhotoPixelError? {
        if (!::photosLibraryClient.isInitialized) {
            Timber.tag(GOOGLE_PHOTOS_TAG).d("library is not initialized")
            initPhotosLibrary()
            Timber.tag(GOOGLE_PHOTOS_TAG).d("library initialized")
        } else {
            Timber.tag(GOOGLE_PHOTOS_TAG).d("library initialized already")
        }

        try {
            val response = photosLibraryClient.listMediaItems()
            // response contains list of photo objects
            val mediaItems = response.iterateAll().toList()
            for (mediaItem in mediaItems) {
                // Access media item properties
                val id = mediaItem.id
                val baseUrl = mediaItem.baseUrl
                // Timber.tag(GOOGLE_PHOTOS_TAG).d("fetchPhotos: each item : $mediaItem")
            }

            Timber.tag(GOOGLE_PHOTOS_TAG).d("Google photos fetched successfully, saving photos metadata to DB")
            savePhotosDataToDB(mediaItems)
            return null
        } catch (e: UnauthenticatedException) {
            Timber.tag(GOOGLE_PHOTOS_TAG).e("fetchPhotos: exception handled ====$e")
            return PhotoPixelError.ExpiredGoogleAuthTokenError
        } catch (e: Exception) {
            Timber.tag(GOOGLE_PHOTOS_TAG).e("fetchPhotos: exception handled ====$e")
            return PhotoPixelError.GenericGoogleError
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
